// filepath: /Users/mac/Documents/게젤샤프트/gesellschaft/src/utils/youtube.ts
export async function fetchLatestVideo(channelId: string): Promise<string | null> {
  const apiKey = import.meta.env.VITE_YOUTUBE_API_KEY;
  const url = `https://www.googleapis.com/youtube/v3/search?key=${apiKey}&channelId=${channelId}&part=snippet&order=date&maxResults=1`;

  const response = await fetch(url);
  const data = await response.json();

  if (data.items && data.items.length > 0) {
    return `https://www.youtube.com/watch?v=${data.items[0].id.videoId}`;
  }

  return null;
}