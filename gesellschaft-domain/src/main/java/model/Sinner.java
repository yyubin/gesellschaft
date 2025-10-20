package model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import model.persona.Persona;

@Getter
public class Sinner {

    private final Long id;
    private final String name;
    private final String nameEn;
    private final List<Persona> personas;

    public Sinner(Long id, String name, String nameEn, List<Persona> personas) {
        this.id = id;
        this.name = name;
        this.nameEn = nameEn;
        this.personas = personas != null ? personas : new ArrayList<>();
    }

}
