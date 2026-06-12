package com.mortenm12.authservice.api;

import com.mortenm12.authservice.api.dto.apikey.ApiKeyCreatedResponse;
import com.mortenm12.authservice.api.dto.apikey.ApiKeyResponse;
import com.mortenm12.authservice.api.dto.apikey.CreateApiKeyRequest;
import com.mortenm12.authservice.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/api-keys")
@Tag(name = "API Keys", description = "API key management for service-to-service authentication")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @GetMapping
    @Operation(summary = "List all API keys (secrets are never returned)")
    public List<ApiKeyResponse> list() {
        return apiKeyService.findAll();
    }

    @PostMapping
    @Operation(summary = "Create an API key — the raw key is returned once and never stored")
    public ResponseEntity<ApiKeyCreatedResponse> create(
            @Valid @RequestBody CreateApiKeyRequest request) {
        ApiKeyCreatedResponse created = apiKeyService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Revoke an API key")
    public ResponseEntity<Void> revoke(@PathVariable UUID id) {
        apiKeyService.revoke(id);
        return ResponseEntity.noContent().build();
    }
}
