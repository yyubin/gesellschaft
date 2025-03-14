<script lang="ts">
  import { onMount } from "svelte";
  import { writable } from "svelte/store";

  const timeLeft = writable("");

  function calculateTimeLeft() {
    const now = new Date();
    const nextThursday = new Date();
    nextThursday.setDate(now.getDate() + ((4 + 7 - now.getDay()) % 7));
    nextThursday.setHours(5, 0, 0, 0);

    const difference = nextThursday.getTime() - now.getTime();

    if (difference > 0) {
      const days = Math.floor(difference / (1000 * 60 * 60 * 24));
      const hours = Math.floor((difference / (1000 * 60 * 60)) % 24);
      const minutes = Math.floor((difference / 1000 / 60) % 60);
      const seconds = Math.floor((difference / 1000) % 60);

      timeLeft.set(
        `${days}:${hours.toString().padStart(2, "0")}:${minutes
          .toString()
          .padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`
      );
    } else {
      timeLeft.set("00:00:00");
    }
  }

  onMount(() => {
    calculateTimeLeft();
    const interval = setInterval(calculateTimeLeft, 1000);
    return () => clearInterval(interval);
  });
</script>

<div class="countdown-timer">
  <div class="red-circle"></div>
  <div class="timer-text">
    <p>거던 초기화까지</p>
    <p class="time">{$timeLeft}</p>
  </div>
</div>

<style>
  .countdown-timer {
    height: 4rem;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    background-color: rgba(59, 59, 61, 0.394);
    padding: 1rem;
    border-radius: 0.5rem;
    flex-direction: row; /* 한 줄로 정렬 */
  }

  .red-circle {
    width: 30px;
    height: 30px;
    background-color: #9c1f03;
    border-radius: 50%;
    margin-right: 1rem;
  }

  .timer-text {
    display: flex;
    flex-direction: row; /* 가로로 배치 */
    align-items: center; /* 세로 정렬 */
    font-size: 2rem;
    color: rgb(229, 227, 227);
    gap: 0.5rem; /* 요소 간 간격 추가 */
  }

  .time {
    color: #9c1f03;
    font-weight: bold;
  }
</style>
