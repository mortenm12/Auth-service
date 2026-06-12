package com.mortenm12.authservice.service;

import com.mortenm12.authservice.api.dto.apikey.ApiKeyCreatedResponse;
import com.mortenm12.authservice.api.dto.apikey.ApiKeyResponse;
import com.mortenm12.authservice.api.dto.apikey.CreateApiKeyRequest;
import com.mortenm12.authservice.domain.ApiKey;
import com.mortenm12.authservice.domain.Organization;
import com.mortenm12.authservice.domain.User;
import com.mortenm12.authservice.exception.ResourceNotFoundException;
import com.mortenm12.authservice.repository.ApiKeyRepository;
import com.mortenm12.authservice.repository.OrganizationRepository;
import com.mortenm12.authservice.repository.UserRepository;
import com.mortenm12.authservice.security.ApiKeyAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ApiKeyService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int KEY_BYTES = 32;

    private final ApiKeyRepository apiKeyRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public ApiKeyService(ApiKeyRepository apiKeyRepository,
            OrganizationRepository organizationRepository,
            UserRepository userRepository) {
        this.apiKeyRepository = apiKeyRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    public List<ApiKeyResponse> findAll() {
        return apiKeyRepository.findAll().stream().map(ApiKeyResponse::from).toList();
    }

    @Transactional
    public ApiKeyCreatedResponse create(CreateApiKeyRequest request) {
        String rawKey = generateRawKey();
        String hash = sha256Hex(rawKey);
        String prefix = rawKey.substring(0, Math.min(10, rawKey.length()));

        Organization org = null;
        if (request.organizationId() != null) {
            org = organizationRepository.findById(request.organizationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Organization", request.organizationId()));
        }

        User user = null;
        if (request.userId() != null) {
            user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.userId()));
        }

        String scopes = request.scopes() != null ? String.join(",", request.scopes()) : "read";
        var apiKey = new ApiKey(request.name(), prefix, hash, scopes, org, user, request.expiresAt());
        ApiKey saved = apiKeyRepository.save(apiKey);
        return ApiKeyCreatedResponse.from(saved, rawKey);
    }

    @Transactional
    public void revoke(UUID id) {
        if (!apiKeyRepository.existsById(id)) {
            throw new ResourceNotFoundException("ApiKey", id);
        }
        apiKeyRepository.deleteById(id);
    }

    @Transactional
    public Authentication authenticate(String rawKey) {
        String hash = sha256Hex(rawKey);
        ApiKey apiKey = apiKeyRepository.findByKeyHash(hash)
                .orElseThrow(() -> new BadCredentialsException("Invalid API key"));
        if (apiKey.isExpired()) {
            throw new BadCredentialsException("API key expired");
        }
        apiKey.recordUsage();
        List<GrantedAuthority> authorities = apiKey.getScopesAsList()
                .stream()
                .map(scope -> (GrantedAuthority) new SimpleGrantedAuthority("SCOPE_" + scope.trim()))
                .toList();
        return new ApiKeyAuthenticationToken(apiKey.getId().toString(), authorities);
    }

    private static String generateRawKey() {
        byte[] bytes = new byte[KEY_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return "sk_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }
}
