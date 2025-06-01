package com.tanzeem.iam.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.tanzeem.iam.entity.*;
import com.tanzeem.iam.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final PermissionRepository permissionRepo;
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TenantSettingsRepository tenantSettingsRepo;

    @Override
    public void run(String... args) {
        final String TENANT_ID = "tanzeem";
        // Step 1: Create Permissions
        List<String> permissionNames = List.of(
                "READ_USERS", "CREATE_USERS", "UPDATE_USERS", "DELETE_USERS"
        );

        Set<Permission> permissions = new HashSet<>();
        for (String name : permissionNames) {
            Permission permission = permissionRepo.findByName(name)
                    .orElseGet(() -> permissionRepo.save(new Permission(name)));
            permissions.add(permission);
        }

        // Step 2: Create Role - ADMIN
        Role adminRole = roleRepo.findByNameAndTenantId("ROLE_ADMIN", TENANT_ID).orElse(null);
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setPermissions(permissions);
            adminRole.setTenantId(TENANT_ID);
            roleRepo.save(adminRole);
        }

        // Step 3: Create Role - USER
        Role userRole = roleRepo.findByNameAndTenantId("ROLE_USER", TENANT_ID).orElse(null);
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setPermissions(Set.of());
            userRole.setTenantId(TENANT_ID);
            roleRepo.save(userRole);
        }

        // Step 4: Create Admin User
        String adminEmail = "admin@erp.com";
        if (!userRepo.existsByEmail(adminEmail)) {
            User adminUser = User.builder()
                    .email(adminEmail)
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Admin User")
                    .enabled(true)
                    .tenantId(TENANT_ID)
                    .roles(Set.of(adminRole))
                    .build();
            userRepo.save(adminUser);
            System.out.println("✅ Admin user created: admin@erp.com / admin123");
        }

        if (!tenantSettingsRepo.existsByTenantId(TENANT_ID)) {
            TenantSettings tenantSettings = new TenantSettings();
            tenantSettings.setTenantId(TENANT_ID);
            tenantSettings.setBusinessType(TenantSettings.BusinessType.PRODUCT);
            tenantSettings.setDefaultCurrency("EGP");
            tenantSettings.setDefaultLanguage("ar");
            tenantSettings.setTimezone("EET");
            tenantSettingsRepo.save(tenantSettings);
            System.out.println("✅ Tenant settings created for tenant: " + TENANT_ID);
        }
    }
}
