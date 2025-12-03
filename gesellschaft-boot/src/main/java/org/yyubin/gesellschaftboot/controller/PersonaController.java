package org.yyubin.gesellschaftboot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yyubin.gesellschaftboot.dto.PersonaResponseDto;
import service.PersonaService;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    @GetMapping("/{id}")
    public ResponseEntity<PersonaResponseDto> getPersona(@PathVariable Long id) {
        var response = personaService.getPersonaById(id);
        return ResponseEntity.ok(PersonaResponseDto.from(response));
    }

    @GetMapping
    public ResponseEntity<List<PersonaResponseDto>> getAllPersonas() {
        var responses = personaService.getAllPersonas();
        return ResponseEntity.ok(
            responses.stream()
                .map(PersonaResponseDto::from)
                .toList()
        );
    }

    @GetMapping("/sinner/{sinnerId}")
    public ResponseEntity<List<PersonaResponseDto>> getPersonasBySinner(@PathVariable Long sinnerId) {
        var responses = personaService.getPersonasBySinnerId(sinnerId);
        return ResponseEntity.ok(
            responses.stream()
                .map(PersonaResponseDto::from)
                .toList()
        );
    }
}
