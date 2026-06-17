package dk.tinker.authservice.api;

import dk.tinker.authservice.api.dto.permission.GrantResourcePermissionRequest;
import dk.tinker.authservice.api.dto.permission.ResourcePermissionResponse;
import dk.tinker.authservice.domain.PermissionLevel;
import dk.tinker.authservice.domain.ResourcePermission;
import dk.tinker.authservice.service.ResourcePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/resource-permissions")
@Tag(name = "Resource Permissions", description = "Resource-level permission management")
public class ResourcePermissionController {

    private final ResourcePermissionService resourcePermissionService;

    public ResourcePermissionController(ResourcePermissionService resourcePermissionService) {
        this.resourcePermissionService = resourcePermissionService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    @Operation(summary = "Grant a resource permission")
    public ResponseEntity<ResourcePermissionResponse> grant(@RequestBody GrantResourcePermissionRequest request) {
        ResourcePermission permission = resourcePermissionService.grant(
                request.userId(),
                request.resourceType(),
                request.resourceId(),
                request.level(),
                request.grantedBy());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(permission.getId())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(permission));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    @Operation(summary = "Revoke a resource permission")
    public ResponseEntity<Void> revoke(@PathVariable UUID id) {
        resourcePermissionService.revoke(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    @Operation(summary = "List permissions for a resource")
    public List<ResourcePermissionResponse> listForResource(
            @RequestParam String resourceType,
            @RequestParam String resourceId) {
        return resourcePermissionService.listForResource(resourceType, resourceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/effective")
    @PreAuthorize("hasAuthority('SCOPE_read')")
    @Operation(summary = "Get effective permission level for a subject on a resource")
    public ResponseEntity<PermissionLevel> effectiveLevel(
            @RequestParam String subject,
            @RequestParam String resourceType,
            @RequestParam String resourceId) {
        return resourcePermissionService.effectiveLevel(UUID.fromString(subject), resourceType, resourceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private ResourcePermissionResponse toResponse(ResourcePermission p) {
        return new ResourcePermissionResponse(
                p.getId(),
                p.getUserId(),
                p.getResourceType(),
                p.getResourceId(),
                p.getLevel(),
                p.getGrantedBy(),
                p.getCreatedAt());
    }
}
