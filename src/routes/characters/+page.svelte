<!-- filepath: /Users/mac/Documents/게젤샤프트/gesellschaft/src/routes/characters/+page.svelte -->
<script lang="ts">
  import { selectedCharacterId } from "../../stores";
  import { goto } from "$app/navigation";

  export let data: {
    characters: { id: number; name: string; name_ko: string }[];
  };

  function handleButtonClick(id: number) {
    console.log("id", id);
    selectedCharacterId.set(id);
    goto("/characters/detail");
  }
</script>

<main class="h-screen flex flex-col justify-center items-center gap-4">
  <div class="grid grid-cols-3 gap-4">
    {#each data.characters as character}
      <button
        class="bg-blue-500 text-white py-2 px-4 rounded"
        on:click={() => handleButtonClick(character.id)}
      >
        {character.name_ko}
      </button>
    {/each}
  </div>
</main>
