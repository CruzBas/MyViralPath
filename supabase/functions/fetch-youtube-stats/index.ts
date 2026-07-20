// @ts-nocheck
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const YOUTUBE_API_BASE = "https://www.googleapis.com/youtube/v3";

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
    const apiKey = body.api_key || Deno.env.get("YOUTUBE_API_KEY");
    const channelId = body.channel_id;

    let stats;

    if (providerToken) {
      stats = await fetchWithOAuth(providerToken);
    } else if (apiKey && channelId) {
      stats = await fetchWithApiKey(apiKey, channelId);
    } else {
      return jsonError("Se requiere provider_token o api_key+channel_id", 400);
    }

    const supabaseAdmin = createClient(
      Deno.env.get("SUPABASE_URL"),
      Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")
    );

    await supabaseAdmin.from("youtube_analytics").upsert({
      user_id: user.id,
      channel_id: stats.channelId,
      channel_title: stats.channelTitle,
      subscriber_count: stats.subscriberCount,
      view_count: stats.viewCount,
      video_count: stats.videoCount,
      avg_views_per_video: stats.avgViewsPerVideo,
      recent_videos: stats.recentVideos,
      top_video_title: stats.topVideoTitle,
      top_video_views: stats.topVideoViews,
      growth_percent: stats.growthPercent,
      fetched_at: new Date().toISOString(),
    }, { onConflict: "user_id" });

    return new Response(JSON.stringify({ success: true, data: stats }), {
      headers: { "Content-Type": "application/json", "Access-Control-Allow-Origin": "*" },
    });
  } catch (err) {
    return jsonError(`Error: ${err.message}`, 500);
  }
});

async function fetchWithOAuth(providerToken) {
  const headers = { Authorization: `Bearer ${providerToken}` };
  const r = await fetch(`${YOUTUBE_API_BASE}/channels?part=snippet,statistics&mine=true`, { headers });
  if (!r.ok) throw new Error(await r.text());
  const d = await r.json();
  if (!d.items?.length) throw new Error("No canal encontrado");
  const ch = d.items[0];
  const videos = await fetchRecentVideos(ch.id, headers, null);
  return buildStats(ch, ch.statistics, videos);
}

async function fetchWithApiKey(apiKey, channelId) {
  const r = await fetch(`${YOUTUBE_API_BASE}/channels?part=snippet,statistics&id=${channelId}&key=${apiKey}`);
  if (!r.ok) throw new Error(await r.text());
  const d = await r.json();
  if (!d.items?.length) throw new Error(`Canal ${channelId} no encontrado`);
  const ch = d.items[0];
  const videos = await fetchRecentVideos(channelId, null, apiKey);
  return buildStats(ch, ch.statistics, videos);
}

async function fetchRecentVideos(channelId, headers, apiKey) {
  try {
    const q = apiKey ? `&key=${apiKey}` : "";
    const r = await fetch(`${YOUTUBE_API_BASE}/search?part=snippet&channelId=${channelId}&type=video&order=date&maxResults=5${q}`, { headers });
    if (!r.ok) return [];
    const sd = await r.json();
    if (!sd.items) return [];
    const ids = sd.items.map(i => i.id.videoId).join(",");
    if (!ids) return [];
    const vr = await fetch(`${YOUTUBE_API_BASE}/videos?part=snippet,statistics&id=${ids}${q}`, { headers });
    if (!vr.ok) return [];
    const vd = await vr.json();
    return (vd.items ?? []).map(v => ({
      videoId: v.id,
      title: v.snippet.title,
      publishedAt: v.snippet.publishedAt,
      thumbnailUrl: v.snippet.thumbnails?.default?.url ?? "",
      views: parseInt(v.statistics.viewCount ?? "0"),
      likes: parseInt(v.statistics.likeCount ?? "0"),
    }));
  } catch { return []; }
}

function buildStats(channel, stats, recentVideos) {
  const subscriberCount = parseInt(stats.subscriberCount ?? "0");
  const viewCount = parseInt(stats.viewCount ?? "0");
  const videoCount = parseInt(stats.videoCount ?? "0");
  const avgViewsPerVideo = videoCount > 0 ? Math.floor(viewCount / videoCount) : 0;
  const topVideo = recentVideos.reduce((max, v) => v.views > (max?.views ?? 0) ? v : max, recentVideos[0] ?? null);
  return {
    channelId: channel.id,
    channelTitle: channel.snippet.title,
    subscriberCount, viewCount, videoCount, avgViewsPerVideo,
    recentVideos,
    topVideoTitle: topVideo?.title ?? "",
    topVideoViews: topVideo?.views ?? 0,
    growthPercent: 0,
    fetchedAt: new Date().toISOString(),
  };
}

function jsonError(message, status) {
  return new Response(JSON.stringify({ success: false, error: message }), {
    status,
    headers: { "Content-Type": "application/json", "Access-Control-Allow-Origin": "*" },
  });
}
