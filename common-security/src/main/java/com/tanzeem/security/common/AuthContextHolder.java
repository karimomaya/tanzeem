package com.tanzeem.security.common;

import java.util.List;

public class AuthContextHolder {
    private static final ThreadLocal<AuthContext> CONTEXT = new ThreadLocal<>();

    public static void setCurrentUser(String username, List<String> roles, String tenantId) {
        CONTEXT.set(new AuthContext(username, roles, tenantId));
    }

    public static AuthContext getCurrentUser() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
