package com.mortenm12.authservice.repository;

import com.mortenm12.authservice.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Organization> findByParentIsNull();

    List<Organization> findByParentId(UUID parentId);
}
