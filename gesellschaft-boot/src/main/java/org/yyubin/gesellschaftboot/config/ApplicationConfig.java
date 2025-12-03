package org.yyubin.gesellschaftboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import port.SinnerRepository;
import service.impl.SinnerServiceImpl;

@Configuration
public class ApplicationConfig {

    @Bean
    public SinnerServiceImpl sinnerService(SinnerRepository sinnerRepository) {
        return new SinnerServiceImpl(sinnerRepository);
    }
}
