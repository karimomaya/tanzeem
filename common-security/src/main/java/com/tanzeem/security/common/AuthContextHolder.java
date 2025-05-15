package com.tanzeem.security.common;

import java.util.List;

public class AuthContextHolder {
    private static final ThreadLocal<AuthContext> CONTEXT = new ThreadLocal<>();

    public static void setCurrentUser(String email, List<String> roles, String tenantId) {
        CONTEXT.set(new AuthContext(email, roles, tenantId));
    }

    public static AuthContext getCurrentUser() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
