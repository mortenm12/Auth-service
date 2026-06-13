package dk.tinker.authservice.api.dto.organization;

import dk.tinker.authservice.domain.Organization;

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
