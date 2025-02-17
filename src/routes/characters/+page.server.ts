import prisma from '$lib/server/prisma';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async () => {
    const characters = await prisma.character.findMany();
    return { characters };
};
