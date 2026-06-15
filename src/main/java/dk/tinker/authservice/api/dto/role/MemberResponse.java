package dk.tinker.authservice.api.dto.role;

import dk.tinker.authservice.domain.UserOrganizationRole;

import java.time.Instant;
import java.util.UUID;

public record MemberResponse(
        UUID membershipId,
        UUID userId,
        String username,
        String email,
        UUID roleId,
        String roleName,
        Instant joinedAt
) {
    public static MemberResponse from(UserOrganizationRole membership) {
        return new MemberResponse(
                membership.getId(),
                membership.getUser().getId(),
                membership.getUser().getUsername(),
                membership.getUser().getEmail(),
                membership.getRole().getId(),
                membership.getRole().getName(),
                membership.getCreatedAt()
        );
    }
}
