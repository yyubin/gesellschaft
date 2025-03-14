<script lang="ts">
  import { onMount } from "svelte";
  import { fetchChannelId, fetchLatestVideo } from "../../utils/youtube";

  let videoUrl: string | null = null;

  onMount(async () => {
    const channelName = "@ProjectMoonOfficial";
    const channelId = await fetchChannelId(channelName);
    if (channelId) {
      videoUrl = await fetchLatestVideo(channelId);
    }
  });
</script>

{#if videoUrl}
  <div class="video-container">
    <iframe
      width="760"
      height="500"
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
