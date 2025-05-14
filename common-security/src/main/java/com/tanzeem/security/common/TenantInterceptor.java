package com.tanzeem.security.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader("X-Tenant-ID");
        if (tenantId != null) {
            AuthContextHolder.setCurrentUser(
                    AuthContextHolder.getCurrentUser().getUsername(),
                    AuthContextHolder.getCurrentUser().getRoles(),
                    tenantId
            );
        }
        return true;
    }
}
