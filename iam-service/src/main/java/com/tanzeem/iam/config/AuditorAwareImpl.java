package com.tanzeem.iam.config;

import com.tanzeem.security.common.AuthContextHolder;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        if (AuthContextHolder.getCurrentUser() != null) {
            return Optional.of(AuthContextHolder.getCurrentUser().getUsername());
        }
        return Optional.of("system");
    }
}
