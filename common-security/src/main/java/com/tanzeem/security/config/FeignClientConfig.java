package com.tanzeem.security.config;

import com.tanzeem.security.common.AuthContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignClientConfig {
    @Bean
    public RequestInterceptor authorizationHeaderInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                var authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && AuthContextHolder.getToken() != null ) {
                    template.header("Authorization", "Bearer " + AuthContextHolder.getToken());
                }
            }
        };
    }
}
