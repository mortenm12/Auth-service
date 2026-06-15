package dk.tinker.authservice.repository;

import dk.tinker.authservice.domain.UserOrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserOrganizationRoleRepository extends JpaRepository<UserOrganizationRole, UUID> {

    @Query("SELECT m FROM UserOrganizationRole m WHERE m.user.id = :userId")
    List<UserOrganizationRole> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT m FROM UserOrganizationRole m WHERE m.organization.id = :organizationId")
    List<UserOrganizationRole> findByOrganizationId(@Param("organizationId") UUID organizationId);

    @Query("SELECT m FROM UserOrganizationRole m WHERE m.user.id = :userId AND m.organization.id = :organizationId")
    Optional<UserOrganizationRole> findByUserIdAndOrganizationId(
            @Param("userId") UUID userId,
            @Param("organizationId") UUID organizationId);

    @Query("SELECT COUNT(m) > 0 FROM UserOrganizationRole m WHERE m.user.id = :userId AND m.organization.id = :organizationId")
    boolean existsByUserIdAndOrganizationId(
            @Param("userId") UUID userId,
            @Param("organizationId") UUID organizationId);

    @Query("SELECT m FROM UserOrganizationRole m JOIN FETCH m.user JOIN FETCH m.role WHERE m.organization.id = :orgId")
    List<UserOrganizationRole> findByOrganizationIdWithDetails(@Param("orgId") UUID orgId);
}
