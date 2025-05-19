import type { PersonaResponse } from "../../services/PersonaService";

interface Props {
    personas: PersonaResponse[];
    onClick?: (id: string) => void;
}

export default function PersonaGrid({ personas, onClick }: Props) {
    return (
      <div className="grid grid-cols-6 gap-1">
        {personas.map((p) => (
          <img
            key={p.id}
            src={p.imagePath}
            alt={p.name}
            onClick={() => onClick?.(p.id.toString())}
            className="w-16 h-16 object-contain cursor-pointer hover:ring-2"
          />
        ))}
      </div>
    );
  }