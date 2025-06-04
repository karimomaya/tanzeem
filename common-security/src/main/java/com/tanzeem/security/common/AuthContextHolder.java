package com.tanzeem.security.common;

import java.util.List;

public class AuthContextHolder {
    private static final ThreadLocal<AuthContext> CONTEXT = new ThreadLocal<>();

    public static void setCurrentUser(String email, List<String> roles, String tenantId, String token) {
        CONTEXT.set(new AuthContext(email, roles, tenantId, token));
    }

    public static String getTenantId() {
        AuthContext authContext = CONTEXT.get();
        return authContext != null ? authContext.getTenantId() : null;
    }

    public static String getToken() {
        AuthContext authContext = CONTEXT.get();
        return authContext != null ? authContext.getToken() : null;
    }

    public static AuthContext getCurrentUser() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
