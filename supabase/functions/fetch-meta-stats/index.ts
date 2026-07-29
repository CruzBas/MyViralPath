// @ts-nocheck
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const FB_API_BASE = "https://graph.facebook.com/v19.0";

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

    let body = {};
    try { body = await req.json(); } catch (_) {}

    const providerToken = body.provider_token;
    if (!providerToken) return jsonError("Se requiere provider_token", 400);

    // 1. Fetch user's Facebook pages
    const pagesRes = await fetch(`${FB_API_BASE}/me/accounts?access_token=${providerToken}`);
    const pagesData = await pagesRes.json();
    if (!pagesData.data || pagesData.data.length === 0) {
      return jsonError("No se encontraron páginas de Facebook vinculadas. Asegúrate de otorgar permisos para gestionar páginas.", 404);
    }
    const page = pagesData.data[0];

    // 2. Try to get linked Instagram Business Account
    const igRes = await fetch(`${FB_API_BASE}/${page.id}?fields=instagram_business_account&access_token=${providerToken}`);
    const igData = await igRes.json();
    
    let followers = 0;
    let likes = 0;
    let comments = 0;
    let pageName = page.name;
    let pageId = page.id;

    if (igData.instagram_business_account) {
      // It's Instagram
      const igId = igData.instagram_business_account.id;
      const metricsRes = await fetch(`${FB_API_BASE}/${igId}?fields=username,followers_count,media.limit(50){like_count,comments_count}&access_token=${providerToken}`);
      const metricsData = await metricsRes.json();
      
      followers = metricsData.followers_count || 0;
      pageName = metricsData.username || pageName;
      pageId = igId;
      
      if (metricsData.media && metricsData.media.data) {
        metricsData.media.data.forEach((m) => {
          likes += (m.like_count || 0);
          comments += (m.comments_count || 0);
        });
      }
    } else {
      // Fallback to Facebook Page metrics
      const fbMetricsRes = await fetch(`${FB_API_BASE}/${page.id}?fields=fan_count,posts.limit(50){likes.summary(true),comments.summary(true)}&access_token=${providerToken}`);
      const fbMetricsData = await fbMetricsRes.json();
      
      followers = fbMetricsData.fan_count || 0;
      
      if (fbMetricsData.posts && fbMetricsData.posts.data) {
        fbMetricsData.posts.data.forEach((p) => {
          likes += (p.likes?.summary?.total_count || 0);
          comments += (p.comments?.summary?.total_count || 0);
        });
      }
    }

    const totalInteractions = likes + comments;

    const supabaseAdmin = createClient(
      Deno.env.get("SUPABASE_URL"),
      Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")
    );

    const stats = {
      user_id: user.id,
      page_id: pageId,
      page_name: pageName,
      followers: followers,
      likes: likes,
      comments: comments,
      total_interactions: totalInteractions,
      fetched_at: new Date().toISOString()
    };

    // Upsert into meta_analytics
    await supabaseAdmin.from("meta_analytics").upsert(stats, { onConflict: "user_id" });

    return new Response(JSON.stringify({ success: true, data: stats }), {
      headers: { "Content-Type": "application/json", "Access-Control-Allow-Origin": "*" },
    });
  } catch (err) {
    return jsonError(`Error: ${err.message}`, 500);
  }
});

function jsonError(message, status) {
  return new Response(JSON.stringify({ success: false, error: message }), {
    status: 200, // Client side parses 200 with success: false
    headers: { "Content-Type": "application/json", "Access-Control-Allow-Origin": "*" },
  });
}
