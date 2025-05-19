import type { JSX } from "react";
import { useState } from "react";

export default function EgoPage(): JSX.Element {
  const [selectedCharacters, setSelectedCharacters] = useState<string[]>([]);
  const [selectedSins, setSelectedSins] = useState<string[]>([]);
  const [selectedKeywords, setSelectedKeywords] = useState<string[]>([]);
  const [selectedAttackTypes, setSelectedAttackTypes] = useState<string[]>([]);

  const toggleSelection = (id: string, list: string[], setter: (v: string[]) => void) => {
    setter(list.includes(id) ? list.filter(i => i !== id) : [...list, id]);
  };

  const toggleCharacter = (id: string) => {
    setSelectedCharacters(prev =>
      prev.includes(id) ? prev.filter(c => c !== id) : [...prev, id]
    );
  };

  const keywordImages = [
    "key_1.webp", "key_2.webp", "key_3.webp", "key_4.webp",
    "key_5.webp", "key_6.webp", "key_7.webp"
  ];

  const characterImages = [
    "cid_1.webp", "cid_2.webp", "cid_3.webp", "cid_4.webp",
    "cid_5.webp", "cid_6.webp", "cid_7.webp", "cid_8.webp",
    "cid_9.webp", "cid_10.webp", "cid_11.webp", "cid_12.webp",
  ];

  const sinImages = [
    "sin_1.webp", "sin_2.webp", "sin_3.webp", "sin_4.webp",
    "sin_5.webp", "sin_6.webp", "sin_7.webp"
  ];

  const typeImages = [
    "type_blunt.webp", "type_pierce.webp", "type_slash.webp"
  ];

  const renderFilterGrid = (
    images: string[],
    basePath: string,
    selectedList: string[],
    setSelectedList: (v: string[]) => void
  ) => (
    <div className="grid grid-cols-4 gap-1">
      {images.map((img, idx) => {
        const id = img.replace(".webp", ""); // 예: "sin_1"
        const isSelected = selectedList.includes(id);

        return (
          <img
            key={idx}
            src={`/src/assets/${basePath}/${img}`}
            alt={`${basePath}-${idx}`}
            onClick={() => toggleSelection(id, selectedList, setSelectedList)}
            className={`w-12 h-12 object-contain rounded shadow-sm cursor-pointer 
              ${isSelected ? "ring-2 ring-blue-500" : ""} transition`}
          />
        );
      })}
    </div>
  );

  return (
    <div className="p-4 max-w-screen-xl mx-auto">
      <h2 className="text-2xl font-bold mb-4">에고 페이지입니다</h2>

      <div className="flex gap-8">
        {/* 왼쪽: 인격 (2줄) */}
        <div className="w-1/2">
          <div className="grid grid-cols-6 grid-rows-2 gap-1">
            {characterImages.map((img, idx) => {
              const id = img.replace(".webp", ""); // 예: "cid_1"
              const isSelected = selectedCharacters.includes(id);

              return (
                <img
                  key={idx}
                  src={`/src/assets/character/${img}`}
                  alt={`character-${idx}`}
                  onClick={() => toggleCharacter(id)}
                  className={`w-16 h-16 object-contain rounded shadow-sm cursor-pointer transition
                    ${isSelected ? "ring-2 ring-blue-500" : ""}`}
                />
              );
            })}
          </div>
        </div>

        {/* 오른쪽: 키워드 + 죄악속성 (가로 배열) + 공격유형 */}
        <div className="w-1/2 space-y-4">
          {renderFilterGrid(keywordImages, "keyword", selectedKeywords, setSelectedKeywords)}
          {renderFilterGrid(sinImages, "sin", selectedSins, setSelectedSins)}
        </div>
        {renderFilterGrid(typeImages, "type", selectedAttackTypes, setSelectedAttackTypes)}
      </div>
    </div>
  );
}