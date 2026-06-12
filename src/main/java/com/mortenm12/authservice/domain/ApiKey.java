package com.mortenm12.authservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "api_keys")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 10)
    private String keyPrefix;

    @Column(nullable = false, unique = true)
    private String keyHash;

    @Column(length = 1000)
    private String scopes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Instant expiresAt;

    private Instant lastUsedAt;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @Version
    private Long version;

    protected ApiKey() { }

    public ApiKey(String name, String keyPrefix, String keyHash, String scopes,
            Organization organization, User user, Instant expiresAt) {
        this.name = name;
        this.keyPrefix = keyPrefix;
        this.keyHash = keyHash;
        this.scopes = scopes;
        this.organization = organization;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public String getKeyHash() {
        return keyHash;
    }

    public String getScopes() {
        return scopes;
    }

    public List<String> getScopesAsList() {
        if (scopes == null || scopes.isBlank()) {
            return List.of();
        }
        return Arrays.asList(scopes.split(","));
    }

    public Organization getOrganization() {
        return organization;
    }

    public User getUser() {
        return user;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public void recordUsage() {
        this.lastUsedAt = Instant.now();
    }
}
