package dk.tinker.authservice.repository;

import dk.tinker.authservice.domain.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

    Optional<ApiKey> findByKeyHash(String keyHash);

    @Query("SELECT k FROM ApiKey k WHERE k.organization.id = :organizationId")
    List<ApiKey> findByOrganizationId(@Param("organizationId") UUID organizationId);

    @Query("SELECT k FROM ApiKey k WHERE k.user.id = :userId")
    List<ApiKey> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT k FROM ApiKey k WHERE k.expiresAt >= :start AND k.expiresAt < :end")
    List<ApiKey> findExpiredBetween(@Param("start") Instant start, @Param("end") Instant end);
}
