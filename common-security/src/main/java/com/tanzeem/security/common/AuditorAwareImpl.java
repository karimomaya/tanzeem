package com.tanzeem.security.common;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public class AuditorAwareImpl  implements AuditorAware<String> {

    public AuditorAwareImpl(){
        System.out.println("is being called");
    }
    @Override
    public Optional<String> getCurrentAuditor() {
        if (AuthContextHolder.getCurrentUser() != null) {
            return Optional.of(AuthContextHolder.getCurrentUser().getUsername());
        }
        return Optional.of("system");
    }
}
