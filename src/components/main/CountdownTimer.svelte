<!-- filepath: /Users/mac/Documents/게젤샤프트/gesellschaft/src/components/CountdownTimer.svelte -->
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
      const hours = Math.floor((difference / (1000 * 60 * 60)) % 24);
      const minutes = Math.floor((difference / 1000 / 60) % 60);
      const seconds = Math.floor((difference / 1000) % 60);

      timeLeft.set(
        `${hours.toString().padStart(2, "0")}:${minutes
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
  <p>Time left until next Thursday 5 AM:</p>
  <p>{$timeLeft}</p>
</div>

<style>
  .countdown-timer {
    text-align: center;
    font-size: 2rem;
    color: white;
    background-color: rgba(0, 0, 0, 0.5);
    padding: 1rem;
    border-radius: 0.5rem;
  }
</style>
