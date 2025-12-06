package org.yyubin.gesellschaftboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import service.PersonaPaginationFacade;
import service.PersonaRecommendationService;
import service.PersonaService;
import service.impl.PersonaPaginationFacadeImpl;

@Configuration
public class FacadeConfig {

    @Bean
    public PersonaPaginationFacade personaPaginationFacade(PersonaService personaService, PersonaRecommendationService personaRecommendationService) {
        return new PersonaPaginationFacadeImpl(personaService, personaRecommendationService);
    }
}
