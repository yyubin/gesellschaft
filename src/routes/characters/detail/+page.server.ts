import prisma from '$lib/server/prisma';
import type { PageServerLoad } from './$types';
import type { character, persona } from '@prisma/client';

export const load: PageServerLoad = async ({ url }) => {
  const characterIdParam = url.searchParams.get("characterId");
  console.log(characterIdParam);
  const personaIdParam = url.searchParams.get("personaId");

  const characters: character[] = await prisma.character.findMany({
    orderBy: { id: 'asc' }
  });

  let personas: persona[] = [];
  let personaDetails: persona | null = null;

  if (characterIdParam) {
    const characterId = Number(characterIdParam);
    console.log("characterId", characterId);
    personas = await prisma.persona.findMany({
      where: { character_id: characterId },
      orderBy: { id: 'asc' }
    });
    console.log("personas", personas);
  }

  if (personaIdParam) {
    const personaId = Number(personaIdParam);
    personaDetails = await prisma.persona.findUnique({
      where: { id: personaId }
    });
  }

  return {
    characters,
    personas,
    personaDetails,
    selectedCharacterId: characterIdParam,
    selectedPersonaId: personaIdParam
  };
};