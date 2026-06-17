package dk.tinker.authservice.repository;

import dk.tinker.authservice.domain.ResourcePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResourcePermissionRepository extends JpaRepository<ResourcePermission, UUID> {

    Optional<ResourcePermission> findByUserIdAndResourceTypeAndResourceId(
            UUID userId, String resourceType, String resourceId);

    List<ResourcePermission> findByResourceTypeAndResourceId(String resourceType, String resourceId);
}
