package com.mortenm12.authservice.api.dto.organization;

import com.mortenm12.authservice.domain.Organization;

import java.time.Instant;
import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        String slug,
        UUID parentId,
        Instant createdAt,
        Instant updatedAt
) {
    public static OrganizationResponse from(Organization org) {
        return new OrganizationResponse(
                org.getId(),
                org.getName(),
                org.getSlug(),
                org.getParentId(),
                org.getCreatedAt(),
                org.getUpdatedAt()
        );
    }
}
