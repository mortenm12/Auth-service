package com.mortenm12.authservice.repository;

import com.mortenm12.authservice.domain.UserOrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserOrganizationRoleRepository extends JpaRepository<UserOrganizationRole, UUID> {

    List<UserOrganizationRole> findByUserId(UUID userId);

    List<UserOrganizationRole> findByOrganizationId(UUID organizationId);

    Optional<UserOrganizationRole> findByUserIdAndOrganizationId(UUID userId, UUID organizationId);

    boolean existsByUserIdAndOrganizationId(UUID userId, UUID organizationId);

    @Query("SELECT m FROM UserOrganizationRole m JOIN FETCH m.user JOIN FETCH m.role WHERE m.organization.id = :orgId")
    List<UserOrganizationRole> findByOrganizationIdWithDetails(@Param("orgId") UUID orgId);
}
