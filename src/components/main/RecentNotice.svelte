<script lang="ts">
  import { onMount } from "svelte";
  import { writable } from "svelte/store";

  // 공지사항을 저장할 store
  const notice = writable<string>("로딩 중...");

  async function fetchNotice() {
    try {
      const response = await fetch("http://localhost:3000/api/notice");
      const data = await response.text(); // HTML 데이터를 가져오기
      notice.set(data);
    } catch (error) {
      console.error("Error fetching notice:", error);
      notice.set("공지사항을 불러오지 못했습니다.");
    }
  }

  onMount(fetchNotice);
</script>

<div
  class="p-4 bg-gray-800 text-white rounded-lg shadow-md max-w-lg notice-container"
>
  <h2 class="text-lg font-bold mb-2">최근 공지</h2>
  <div class="text-sm notice-content">
    {#if $notice}
      {@html $notice}
    {:else}
      <p>공지 없음</p>
    {/if}
  </div>
</div>

<style>
  .notice-container {
    max-width: 800px;
    width: 100%;
    min-height: 600px;
    height: 800px;
    overflow-y: auto;
    padding: 16px;
    box-sizing: border-box;
    word-wrap: break-word;
    white-space: normal;
  }

  .notice-content img {
    max-width: 100%;
    height: auto;
  }

  .notice-content iframe {
    max-width: 100%;
  }
</style>
