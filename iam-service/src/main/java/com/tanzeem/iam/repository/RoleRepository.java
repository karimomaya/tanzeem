package com.tanzeem.iam.repository;

import com.tanzeem.iam.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNameAndTenantId(String name, String tenantId);
}