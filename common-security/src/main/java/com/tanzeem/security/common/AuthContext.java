package com.tanzeem.security.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class AuthContext {
    private String username;
    private List<String> roles;
    private String tenantId;
}
