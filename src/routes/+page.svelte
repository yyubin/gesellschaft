<!-- filepath: /Users/mac/Documents/게젤샤프트/gesellschaft/src/routes/+page.svelte -->
<script lang="ts">
  import CountdownTimer from "../components/main/CountdownTimer.svelte";
  import LatestVideo from "../components/main/LatestVideo.svelte";
  import RecentNotice from "../components/main/RecentNotice.svelte";
  import Footer from "../components/common/Footer.svelte";
</script>

<main class="flex flex-col justify-center items-center bg-black">
  <CountdownTimer />
  <LatestVideo />
</main>

<Footer />

<style>
  html,
  body {
    height: 100%;
    background-color: black; /* 💡 공지가 늘어나도 배경색 유지 */
    margin: 0;
    padding: 0;
  }

  main {
    display: flex;
    flex-direction: column; /* 💡 세로 정렬 */
    justify-content: flex-start; /* 💡 위에서 아래로 자연스럽게 배치 */
    align-items: center; /* 💡 좌우 중앙 정렬 */
    min-height: 100%;
    width: 100%;
    text-align: center;
    padding: 20px; /* 💡 공지가 커지면 너무 딱 붙는 걸 방지 */
    box-sizing: border-box;
  }
</style>
