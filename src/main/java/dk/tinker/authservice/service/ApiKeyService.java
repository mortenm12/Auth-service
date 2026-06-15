package dk.tinker.authservice.service;

import dk.tinker.authservice.api.dto.apikey.ApiKeyCreatedResponse;
import dk.tinker.authservice.api.dto.apikey.ApiKeyResponse;
import dk.tinker.authservice.api.dto.apikey.CreateApiKeyRequest;
import dk.tinker.authservice.api.dto.apikey.ValidateApiKeyResponse;
import dk.tinker.authservice.domain.ApiKey;
import dk.tinker.authservice.domain.Organization;
import dk.tinker.authservice.domain.User;
import dk.tinker.authservice.event.TokenEventPublisher;
import dk.tinker.authservice.exception.ResourceNotFoundException;
import dk.tinker.authservice.repository.ApiKeyRepository;
import dk.tinker.authservice.repository.OrganizationRepository;
import dk.tinker.authservice.repository.UserRepository;
import dk.tinker.authservice.security.ApiKeyAuthenticationToken;
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
    private final TokenEventPublisher tokenEventPublisher;

    public ApiKeyService(ApiKeyRepository apiKeyRepository,
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            TokenEventPublisher tokenEventPublisher) {
        this.apiKeyRepository = apiKeyRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.tokenEventPublisher = tokenEventPublisher;
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
        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey", id));
        String keyHash = apiKey.getKeyHash();
        apiKeyRepository.delete(apiKey);
        tokenEventPublisher.publishApiKeyRevoked(keyHash);
    }

    public ValidateApiKeyResponse validateForExternal(String rawKey) {
        String hash = sha256Hex(rawKey);
        return apiKeyRepository.findByKeyHash(hash)
                .map(apiKey -> {
                    if (apiKey.isExpired()) {
                        return new ValidateApiKeyResponse(false, hash, null, null, List.of(), null);
                    }
                    UUID userId = apiKey.getUser() != null ? apiKey.getUser().getId() : null;
                    UUID orgId = apiKey.getOrganization() != null ? apiKey.getOrganization().getId() : null;
                    return new ValidateApiKeyResponse(
                            true,
                            hash,
                            userId,
                            orgId,
                            apiKey.getScopesAsList(),
                            apiKey.getExpiresAt()
                    );
                })
                .orElse(new ValidateApiKeyResponse(false, hash, null, null, List.of(), null));
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
