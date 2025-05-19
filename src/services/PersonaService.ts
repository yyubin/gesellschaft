import api from "../lib/axios";

export interface PersonaSearchRequest {
    characterIds: string[];
    sinIds: string[];
    keywordIds: string[];
    attackTypes: string[];
    page: number;
    size: number;
}

export interface PersonaResponse {
    id: number;
    name: string;
    imagePath: string;
  }

export async function searchPersonas(req: PersonaSearchRequest) {
    const response = await api.post<{ content: PersonaResponse[]; totalPages: number; totalElements: number }>(
      "/personas/search",
      req
    );
  
    return response.data; // Page<PersonaResponse>
  }
