<script lang="ts">
  import { onMount } from "svelte";
  import { fetchChannelId, fetchLatestVideo } from "../../utils/youtube";

  let videoUrl: string | null = null;

  onMount(async () => {
    const channelName = "@ProjectMoonOfficial"; // 유튜브 채널 이름을 여기에 입력하세요
    const channelId = await fetchChannelId(channelName);
    if (channelId) {
      videoUrl = await fetchLatestVideo(channelId);
    }
  });
</script>

{#if videoUrl}
  <div class="video-container">
    <iframe
      width="560"
      height="315"
      src={videoUrl}
      frameborder="0"
      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
      allowfullscreen
    ></iframe>
  </div>
{:else}
  <p>Loading latest video...</p>
{/if}

<style>
  .video-container {
    margin-top: 2rem;
  }
</style>
