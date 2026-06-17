package dk.tinker.authservice.service;

import dk.tinker.authservice.domain.PermissionLevel;
import dk.tinker.authservice.domain.ResourcePermission;
import dk.tinker.authservice.event.TokenInvalidationPublisher;
import dk.tinker.authservice.exception.ResourceNotFoundException;
import dk.tinker.authservice.repository.ResourcePermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ResourcePermissionService {

    private final ResourcePermissionRepository resourcePermissionRepository;
    private final TokenInvalidationPublisher tokenInvalidationPublisher;

    public ResourcePermissionService(ResourcePermissionRepository resourcePermissionRepository,
                                     TokenInvalidationPublisher tokenInvalidationPublisher) {
        this.resourcePermissionRepository = resourcePermissionRepository;
        this.tokenInvalidationPublisher = tokenInvalidationPublisher;
    }

    @Transactional
    public ResourcePermission grant(UUID userId, String resourceType, String resourceId,
                                    PermissionLevel level, UUID grantedBy) {
        ResourcePermission permission = resourcePermissionRepository
                .findByUserIdAndResourceTypeAndResourceId(userId, resourceType, resourceId)
                .map(existing -> {
                    existing.setLevel(level);
                    existing.setGrantedBy(grantedBy);
                    return existing;
                })
                .orElseGet(() -> new ResourcePermission(userId, resourceType, resourceId, level, grantedBy));

        ResourcePermission saved = resourcePermissionRepository.save(permission);
        tokenInvalidationPublisher.publishResourcePermissionChanged(userId.toString(), resourceType, resourceId);
        return saved;
    }

    @Transactional
    public void revoke(UUID id) {
        ResourcePermission permission = resourcePermissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResourcePermission", id));
        tokenInvalidationPublisher.publishResourcePermissionChanged(
                permission.getUserId().toString(),
                permission.getResourceType(),
                permission.getResourceId());
        resourcePermissionRepository.delete(permission);
    }

    public Optional<PermissionLevel> effectiveLevel(UUID userId, String resourceType, String resourceId) {
        return resourcePermissionRepository
                .findByUserIdAndResourceTypeAndResourceId(userId, resourceType, resourceId)
                .map(ResourcePermission::getLevel);
    }

    public List<ResourcePermission> listForResource(String resourceType, String resourceId) {
        return resourcePermissionRepository.findByResourceTypeAndResourceId(resourceType, resourceId);
    }
}
