<script lang="ts">
  export let data: {
    characters: { id: number; name: string; name_ko: string }[];
    personas: {
      id: number;
      character_id: number;
      rank: number;
      persona_name: string;
      health: number;
      min_speed: number;
      max_speed: number;
      defence_level: number;
      attack_resistance: string;
      penetration_resistance: string;
      batting_resistance: string;
      season: string;
      release_date: string;
      mental_strength: number;
      sync: number;
    }[];
    personaDetails: {
      id: number;
      character_id: number;
      rank: number;
      persona_name: string;
      health: number;
      min_speed: number;
      max_speed: number;
      defence_level: number;
      attack_resistance: string;
      penetration_resistance: string;
      batting_resistance: string;
      season: string;
      release_date: string;
      mental_strength: number;
      sync: number;
    } | null;
    selectedCharacterId: string | null;
    selectedPersonaId: string | null;
  };

  let showSync3 = false;

  function toggleSync() {
    showSync3 = !showSync3;
  }
</script>

<div class="container">
  <!-- 왼쪽: 캐릭터 리스트 -->
  <div class="sidebar">
    <h2>Characters</h2>
    {#each data.characters as character}
      <a href="?characterId={character.id}">
        <button
          class={data.selectedCharacterId == character.id.toString()
            ? "selected"
            : "unselected"}
        >
          {character.name_ko}
        </button>
      </a>
    {/each}
  </div>

  <!-- 나머지 영역: 상단과 메인 -->
  <div class="content">
    <!-- 상단: 선택된 캐릭터의 인격 리스트 -->
    <div class="topbar">
      <h2>Personas</h2>
      {#if data.personas.length > 0}
        {#each data.personas.filter((persona) => persona.sync === 4) as persona}
          <a href="?characterId={persona.character_id}&personaId={persona.id}">
            <button
              class={data.selectedPersonaId == persona.id.toString()
                ? "selected"
                : "unselected"}
            >
              {persona.persona_name}
            </button>
          </a>
        {/each}
      {:else}
        <p>Select a character to see its personas.</p>
      {/if}
    </div>

    <!-- 메인: 선택한 인격의 상세 정보 -->
    <div class="main">
      {#if data.personaDetails}
        <h2>Details: {data.personaDetails.persona_name}</h2>
        <button on:click={toggleSync}>
          {showSync3 ? "Show Sync 4" : "Show Sync 3"}
        </button>
        <ul>
          <li><strong>ID:</strong> {data.personaDetails.id}</li>
          <li><strong>Rank:</strong> {data.personaDetails.rank}</li>
          <li><strong>Health:</strong> {data.personaDetails.health}</li>
          <li>
            <strong>Speed:</strong>
            {data.personaDetails.min_speed} - {data.personaDetails.max_speed}
          </li>
          <li>
            <strong>Defence Level:</strong>
            {data.personaDetails.defence_level}
          </li>
          <li>
            <strong>Attack Resistance:</strong>
            {data.personaDetails.attack_resistance}
          </li>
          <li>
            <strong>Penetration Resistance:</strong>
            {data.personaDetails.penetration_resistance}
          </li>
          <li>
            <strong>Batting Resistance:</strong>
            {data.personaDetails.batting_resistance}
          </li>
          <li><strong>Season:</strong> {data.personaDetails.season}</li>
          <li>
            <strong>Release Date:</strong>
            {data.personaDetails.release_date}
          </li>
          <li>
            <strong>Mental Strength:</strong>
            {data.personaDetails.mental_strength}
          </li>
          <li><strong>Sync:</strong> {data.personaDetails.sync}</li>
        </ul>
      {:else}
        <p>Select a persona to view its details.</p>
      {/if}
    </div>
  </div>
</div>

<style>
  .container {
    display: flex;
    height: 100vh;
  }
  .sidebar {
    width: 200px;
    border-right: 1px solid #ddd;
    padding: 1rem;
    overflow-y: auto;
  }
  .content {
    flex: 1;
    display: flex;
    flex-direction: column;
  }
  .topbar {
    border-bottom: 1px solid #ddd;
    padding: 1rem;
    overflow-x: auto;
  }
  .main {
    flex: 1;
    padding: 1rem;
    overflow-y: auto;
  }
  button {
    margin: 0.25rem;
    padding: 0.5rem 1rem;
    border: none;
    border-radius: 0.25rem;
    cursor: pointer;
  }
  .selected {
    background-color: #3b82f6;
    color: white;
  }
  .unselected {
    background-color: #e5e7eb;
    color: #1f2937;
  }
</style>
