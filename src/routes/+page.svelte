<!-- filepath: /Users/mac/Documents/게젤샤프트/gesellschaft/src/routes/+page.svelte -->
<script lang="ts">
  import CountdownTimer from "../components/main/CountdownTimer.svelte";
  import LatestVideo from "../components/main/LatestVideo.svelte";
  import RecentNotice from "../components/main/RecentNotice.svelte";
</script>

<main class="h-screen flex flex-col justify-center items-center bg-black">
  <CountdownTimer />
  <LatestVideo />
  <RecentNotice />
</main>
