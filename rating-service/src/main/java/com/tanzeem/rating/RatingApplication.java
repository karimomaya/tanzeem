package com.tanzeem.rating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication(scanBasePackages = {"com.tanzeem.rating", "com.tanzeem.security.common", "com.tanzeem.common"})
@EnableJpaAuditing
@EnableCaching
public class RatingApplication {
    public static void main(String[] args) {
        SpringApplication.run(RatingApplication.class, args);
    }
}
