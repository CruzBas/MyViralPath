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
      Deno.env.get("SUPABASE_URL")!,
      Deno.env.get("SUPABASE_ANON_KEY")!,
      { global: { headers: { Authorization: authHeader } } }
    );

    const { data: { user }, error: userError } = await supabaseClient.auth.getUser();
    if (userError || !user) return jsonError("Unauthorized", 401);

    const GEMINI_API_KEY = Deno.env.get("GEMINI_API_KEY");
    if (!GEMINI_API_KEY) {
      return jsonError("GEMINI_API_KEY is not configured", 500);
    }

    const supabaseAdmin = createClient(
      Deno.env.get("SUPABASE_URL")!,
      Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!
    );

    // 1. Fetch user context (resilient - won't fail if data is missing)
    const [
      { data: nicheData },
      { data: platformsData },
      { data: audienceData },
      { data: ytData },
      { data: metaData }
    ] = await Promise.all([
      supabaseAdmin.from('user_niches').select('niche, custom_niche').eq('user_id', user.id).maybeSingle(),
      supabaseAdmin.from('user_platforms').select('platform').eq('user_id', user.id),
      supabaseAdmin.from('audience_targets').select('country_name, is_global, age_min, age_max, gender').eq('user_id', user.id).maybeSingle(),
      supabaseAdmin.from('youtube_analytics').select('subscriber_count, view_count, channel_title').eq('user_id', user.id).maybeSingle(),
      supabaseAdmin.from('meta_analytics').select('followers, total_interactions, page_name').eq('user_id', user.id).maybeSingle(),
    ]);

    const niche = nicheData?.custom_niche || nicheData?.niche || "Contenido General";
    const platforms = platformsData?.map((p: any) => p.platform).join(', ') || "Instagram, TikTok";
    const country = audienceData?.is_global ? "Global" : (audienceData?.country_name || "Global");
    const age = audienceData ? `${audienceData.age_min || 18}-${audienceData.age_max || 35} años` : "18-35 años";
    const gender = audienceData?.gender || "Todos";
    const ytSubs = ytData?.subscriber_count || 0;
    const ytViews = ytData?.view_count || 0;
    const metaFollowers = metaData?.followers || 0;
    const metaInteractions = metaData?.total_interactions || 0;

    // 2. Build Prompt for Gemini
    const prompt = `Eres un experto Content Manager. Genera un plan estratégico DIARIO para un creador de contenido.

DATOS DEL CREADOR:
- Nicho: ${niche}
- Plataformas: ${platforms}
- Audiencia: ${country}, ${age}, Género: ${gender}
- YouTube: ${ytSubs} suscriptores, ${ytViews} vistas totales
- Meta (Instagram/Facebook): ${metaFollowers} seguidores, ${metaInteractions} interacciones

INSTRUCCIÓN: Responde SOLO con JSON válido, sin texto adicional, sin markdown, sin explicaciones. El JSON debe tener exactamente esta estructura:
{
  "best_posting_time": "HH:MM",
  "opportunity_score": 75,
  "competition_level": "Medio",
  "growth_potential": "Descripción breve del potencial de crecimiento",
  "content_ideas": [
    {
      "type": "REEL",
      "title": "Título específico para el nicho",
      "platform": "Instagram",
      "recommended_time": "18:00",
      "status": "PROGRAMADO"
    },
    {
      "type": "VIDEO",
      "title": "Título específico para el nicho",
      "platform": "YouTube",
      "recommended_time": "19:00",
      "status": "BORRADOR"
    }
  ],
  "next_steps": [
    "Tarea accionable y específica 1",
    "Tarea accionable y específica 2",
    "Tarea accionable y específica 3"
  ]
}

Personaliza el contenido al nicho "${niche}" y plataformas "${platforms}". Genera exactamente 2 content_ideas y 3 next_steps.`;

    const geminiUrl = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${GEMINI_API_KEY}`;

    const geminiRes = await fetch(geminiUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        contents: [{ parts: [{ text: prompt }] }],
        generationConfig: {
          temperature: 0.7,
          maxOutputTokens: 1024,
        }
      })
    });

    if (!geminiRes.ok) {
      const errText = await geminiRes.text();
      console.error("Gemini API error:", geminiRes.status, errText);
      return jsonError(`Gemini API error: ${geminiRes.status}`, 500);
    }

    const geminiData = await geminiRes.json();
    const rawText = geminiData?.candidates?.[0]?.content?.parts?.[0]?.text;

    if (!rawText) {
      console.error("No text in Gemini response:", JSON.stringify(geminiData));
      return jsonError("No response from AI", 500);
    }

    let aiJson: any;
    try {
      const cleaned = rawText.replace(/```json\s*/g, '').replace(/```\s*/g, '').trim();
      aiJson = JSON.parse(cleaned);
    } catch (e) {
      console.error("JSON parse error. Raw:", rawText);
      return jsonError("AI returned invalid JSON", 500);
    }


    const today = new Date().toISOString().split('T')[0];

    const { data: existingPlan } = await supabaseAdmin
      .from('strategic_plans')
      .select('id')
      .eq('user_id', user.id)
      .eq('plan_date', today)
      .maybeSingle();


    const planPayload: any = {
      user_id: user.id,
      plan_date: today,
      content_ideas: aiJson.content_ideas || [],
      best_posting_time: aiJson.best_posting_time || null,
      opportunity_score: aiJson.opportunity_score || null,
      competition_level: aiJson.competition_level || null,
      growth_potential: aiJson.growth_potential || null,
      updated_at: new Date().toISOString()
    };

    let planId: string;

    if (existingPlan) {
      planId = existingPlan.id;
      const { error: updateErr } = await supabaseAdmin
        .from('strategic_plans')
        .update(planPayload)
        .eq('id', planId);
      if (updateErr) {
        console.error("Update error:", updateErr);
        return jsonError(`DB update error: ${updateErr.message}`, 500);
      }
      // Delete old tasks for today
      await supabaseAdmin.from('next_steps').delete().eq('plan_id', planId);
    } else {
      const { data: newPlan, error: insertErr } = await supabaseAdmin
        .from('strategic_plans')
        .insert(planPayload)
        .select('id')
        .single();
      if (insertErr) {
        console.error("Insert error:", insertErr);
        return jsonError(`DB insert error: ${insertErr.message}`, 500);
      }
      planId = newPlan.id;
    }

    // Insert next steps
    const steps = (aiJson.next_steps || []).map((title: string, idx: number) => ({
      user_id: user.id,
      plan_id: planId,
      title: String(title),
      is_completed: false,
      order_index: idx,
    }));

    if (steps.length > 0) {
      const { error: stepsErr } = await supabaseAdmin.from('next_steps').insert(steps);
      if (stepsErr) {
        console.error("Steps insert error:", stepsErr);
        // Not fatal — plan was saved
      }
    }

    return new Response(JSON.stringify({ success: true, planId }), {
      headers: { "Content-Type": "application/json", "Access-Control-Allow-Origin": "*" },
    });

  } catch (err: any) {
    console.error("Unhandled error:", err);
    return jsonError(`Internal error: ${err.message}`, 500);
  }
});

function jsonError(message: string, status: number): Response {
  return new Response(JSON.stringify({ success: false, error: message }), {
    status,
    headers: { "Content-Type": "application/json", "Access-Control-Allow-Origin": "*" },
  });
}
