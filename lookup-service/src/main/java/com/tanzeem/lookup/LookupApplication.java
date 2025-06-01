package com.tanzeem.lookup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication(scanBasePackages = {"com.tanzeem.lookup", "com.tanzeem.security.common"})
@EnableJpaAuditing
public class LookupApplication {
    public static void main(String[] args) {
        SpringApplication.run(LookupApplication.class, args);
    }
}
