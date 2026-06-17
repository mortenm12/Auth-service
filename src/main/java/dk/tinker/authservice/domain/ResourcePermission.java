package dk.tinker.authservice.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resource_permissions")
public class ResourcePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "resource_type", nullable = false)
    private String resourceType;

    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, columnDefinition = "permission_level")
    private PermissionLevel level;

    @Column(name = "granted_by", nullable = false)
    private UUID grantedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ResourcePermission() {}

    public ResourcePermission(UUID userId, String resourceType, String resourceId, PermissionLevel level, UUID grantedBy) {
        this.userId = userId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.level = level;
        this.grantedBy = grantedBy;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getResourceType() { return resourceType; }
    public String getResourceId() { return resourceId; }
    public PermissionLevel getLevel() { return level; }
    public UUID getGrantedBy() { return grantedBy; }
    public Instant getCreatedAt() { return createdAt; }

    public void setLevel(PermissionLevel level) { this.level = level; }
    public void setGrantedBy(UUID grantedBy) { this.grantedBy = grantedBy; }
}
