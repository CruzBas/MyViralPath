// @ts-nocheck
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

Deno.serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", {
      headers: {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
      },
    });
  }

  try {
    const authHeader = req.headers.get("Authorization");
    if (!authHeader) return jsonError("No authorization header", 401);

    const supabaseClient = createClient(
      Deno.env.get("SUPABASE_URL"),
      Deno.env.get("SUPABASE_ANON_KEY"),
      { global: { headers: { Authorization: authHeader } } }
    );

    const { data: { user }, error: userError } = await supabaseClient.auth.getUser();
    if (userError || !user) return jsonError("Unauthorized", 401);

    const GEMINI_API_KEY = Deno.env.get("GEMINI_API_KEY");
    if (!GEMINI_API_KEY) {
       return jsonError("GEMINI_API_KEY is not set in secrets", 500);
    }

    const supabaseAdmin = createClient(
      Deno.env.get("SUPABASE_URL"),
      Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")
    );

    // 1. Fetch user context
    const [
      { data: niches },
      { data: platforms },
      { data: audiences },
      { data: ytAnalytics },
      { data: metaAnalytics }
    ] = await Promise.all([
      supabaseAdmin.from('user_niches').select('*').eq('user_id', user.id).maybeSingle(),
      supabaseAdmin.from('user_platforms').select('*').eq('user_id', user.id),
      supabaseAdmin.from('audience_targets').select('*').eq('user_id', user.id).maybeSingle(),
      supabaseAdmin.from('youtube_analytics').select('*').eq('user_id', user.id).maybeSingle(),
      supabaseAdmin.from('meta_analytics').select('*').eq('user_id', user.id).maybeSingle()
    ]);

    const context = {
      niche: niches?.niche || niches?.custom_niche || "General",
      platforms: platforms?.map(p => p.platform) || ["Tiktok", "Instagram", "Youtube"],
      audience: audiences || { country: "Global", age: "Todas las edades", gender: "Todos" },
      youtube: ytAnalytics || { subscriber_count: 0, view_count: 0 },
      meta: metaAnalytics || { followers: 0, total_interactions: 0 }
    };

    // 2. Build Prompt for Gemini
    const prompt = `
      Eres un experto Content Manager y estratega de redes sociales. Tu tarea es crear un plan estratégico DIARIO para un creador de contenido con el siguiente contexto:
      
      Nicho: ${context.niche}
      Plataformas Objetivo: ${context.platforms.join(', ')}
      Audiencia: País ${context.audience.country}, Edades ${context.audience.age}, Género ${context.audience.gender}
      Métricas Actuales:
      - YouTube: ${context.youtube.subscriber_count || 0} subs, ${context.youtube.view_count || 0} vistas
      - Meta (Insta/FB): ${context.meta.followers || 0} followers, ${context.meta.total_interactions || 0} interacciones

      Devuelve ÚNICAMENTE un objeto JSON válido con la siguiente estructura, sin texto adicional ni formateo markdown fuera del JSON:
      {
        "best_posting_time": "ej: 18:00",
        "recommended_platform": "ej: Instagram",
        "opportunity_score": 85,
        "competition_level": "Alto/Medio/Bajo",
        "growth_potential": "ej: Viralidad mediante audios en tendencia",
        "content_ideas": [
          {
            "type": "REEL",
            "title": "Idea del contenido",
            "platform": "Instagram",
            "recommended_time": "18:00",
            "status": "PROGRAMADO"
          }
        ],
        "next_steps": [
          "Tarea clave accionable 1",
          "Tarea clave accionable 2",
          "Tarea clave accionable 3"
        ]
      }
      
      Asegúrate de que las ideas de contenido (al menos 2) y los "next_steps" (al menos 3) sean altamente específicos al nicho y plataformas.
    `;

    // 3. Call Gemini API
    const geminiUrl = `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${GEMINI_API_KEY}`;
    
    const geminiRes = await fetch(geminiUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        contents: [{ parts: [{ text: prompt }] }],
        generationConfig: {
            temperature: 0.7,
            responseMimeType: "application/json"
        }
      })
    });

    const geminiData = await geminiRes.json();
    if (!geminiRes.ok) {
       console.error("Gemini API Error:", geminiData);
       return jsonError("Error calling AI API", 500);
    }

    const aiText = geminiData.candidates[0].content.parts[0].text;
    let aiJson;
    try {
      const cleanJson = aiText.replace(/```json/g, '').replace(/```/g, '').trim();
      aiJson = JSON.parse(cleanJson);
    } catch (e) {
      console.error("Parse error. AI Response:", aiText);
      return jsonError("Invalid JSON from AI", 500);
    }

    // 4. Save to Database
    const today = new Date().toISOString().split('T')[0];
    
    const { data: existingPlan } = await supabaseAdmin
      .from('strategic_plans')
      .select('id')
      .eq('user_id', user.id)
      .eq('plan_date', today)
      .maybeSingle();

    let planId;

    const planData = {
      user_id: user.id,
      plan_date: today,
      content_ideas: aiJson.content_ideas,
      best_posting_time: aiJson.best_posting_time,
      recommended_platform: aiJson.recommended_platform,
      opportunity_score: aiJson.opportunity_score,
      competition_level: aiJson.competition_level,
      growth_potential: aiJson.growth_potential,
      updated_at: new Date().toISOString()
    };

    if (existingPlan) {
      planId = existingPlan.id;
      await supabaseAdmin.from('strategic_plans').update(planData).eq('id', planId);
      await supabaseAdmin.from('next_steps').delete().eq('plan_id', planId);
    } else {
      const { data: newPlan, error: insertError } = await supabaseAdmin
        .from('strategic_plans')
        .insert(planData)
        .select('id')
        .single();
        
      if (insertError) throw insertError;
      planId = newPlan.id;
    }

    const tasksData = aiJson.next_steps.map((task, index) => ({
      user_id: user.id,
      plan_id: planId,
      title: task,
      is_completed: false,
      order_index: index
    }));

    await supabaseAdmin.from('next_steps').insert(tasksData);

    return new Response(JSON.stringify({ success: true, planId: planId }), {
      headers: { "Content-Type": "application/json", "Access-Control-Allow-Origin": "*" },
    });

  } catch (err) {
    console.error(err);
    return jsonError(`Error: ${err.message}`, 500);
  }
});

function jsonError(message, status) {
  return new Response(JSON.stringify({ success: false, error: message }), {
    status: status,
    headers: { "Content-Type": "application/json", "Access-Control-Allow-Origin": "*" },
  });
}
