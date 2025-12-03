package org.yyubin.gesellschaftboot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yyubin.gesellschaftboot.dto.SinnerResponseDto;
import service.SinnerService;
import service.impl.SinnerServiceImpl;

@RestController
@RequestMapping("/api/sinners")
@RequiredArgsConstructor
public class SinnerController {

    private final SinnerService sinnerService;

    @GetMapping("/{id}")
    public ResponseEntity<SinnerResponseDto> getSinner(@PathVariable Long id) {
        var response = sinnerService.getSinnerById(id);
        return ResponseEntity.ok(SinnerResponseDto.from(response));
    }

}
