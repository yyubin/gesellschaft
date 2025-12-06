package org.yyubin.gesellschaftboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import port.PersonaRecommendationRepository;
import port.PersonaRepository;
import port.SinnerRepository;
import service.PersonaRecommendationService;
import service.SinnerService;
import service.impl.PersonaRecommendationServiceImpl;
import service.impl.PersonaServiceImpl;
import service.impl.SinnerServiceImpl;

@Configuration
public class ApplicationConfig {

    @Bean
    public SinnerServiceImpl sinnerService(SinnerRepository sinnerRepository) {
        return new SinnerServiceImpl(sinnerRepository);
    }

    @Bean
    public PersonaServiceImpl personaService(PersonaRepository personaRepository, SinnerService sinnerService) {
        return new PersonaServiceImpl(personaRepository, sinnerService);
    }

    @Bean
    public PersonaRecommendationService personaRecommendationService(PersonaRecommendationRepository personaRecommendationRepository) {
        return new PersonaRecommendationServiceImpl(personaRecommendationRepository);
    }
}
