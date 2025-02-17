import prisma from '$lib/server/prisma';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ url }) => {
  const characterIdParam = url.searchParams.get("characterId");
  const personaIdParam = url.searchParams.get("personaId");

  const characters = await prisma.character.findMany({
    orderBy: { id: 'asc' }
  });

  let personas = [];
  let personaDetails = null;

  if (characterIdParam) {
    const characterId = Number(characterIdParam);
    personas = await prisma.persona.findMany({
      where: { character_id: characterId },
      orderBy: { id: 'asc' }
    });
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
