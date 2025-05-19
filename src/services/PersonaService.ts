export interface PersonaSearchRequest {
    characterIds: string[];
    sinIds: string[];
    keywordIds: string[];
    attackTypes: string[];
    page: number;
    size: number;
}

export async function searchPersonas(req: PersonaSearchRequest) {
    const response = await fetch("/api/personas/search", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(req),
    });
  
    if (!response.ok) {
      throw new Error("검색 요청 실패");
    }
  
    return await response.json(); // 응답: Page<PersonaResponse> 형태 예상
}
