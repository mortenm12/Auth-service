package dk.tinker.authservice.api;

import dk.tinker.authservice.api.dto.organization.CreateOrganizationRequest;
import dk.tinker.authservice.api.dto.organization.OrganizationResponse;
import dk.tinker.authservice.api.dto.organization.UpdateOrganizationRequest;
import dk.tinker.authservice.api.dto.role.AssignRoleRequest;
import dk.tinker.authservice.api.dto.role.MemberResponse;
import dk.tinker.authservice.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
@Tag(name = "Organizations", description = "Organization and sub-organization management")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    @Operation(summary = "List root organizations")
    public List<OrganizationResponse> listRoots() {
        return organizationService.findRoots();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get organization by ID")
    public OrganizationResponse getById(@PathVariable UUID id) {
        return organizationService.findById(id);
    }

    @GetMapping("/{id}/children")
    @Operation(summary = "List sub-organizations")
    public List<OrganizationResponse> listChildren(@PathVariable UUID id) {
        return organizationService.findChildren(id);
    }

    @PostMapping
    @Operation(summary = "Create an organization")
    public ResponseEntity<OrganizationResponse> create(
            @Valid @RequestBody CreateOrganizationRequest request) {
        OrganizationResponse created = organizationService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an organization")
    public OrganizationResponse update(@PathVariable UUID id,
            @Valid @RequestBody UpdateOrganizationRequest request) {
        return organizationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an organization")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        organizationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    @Operation(summary = "List members of an organization")
    public List<MemberResponse> listMembers(@PathVariable UUID id) {
        return organizationService.findMembers(id);
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "Assign a user to an organization with a role")
    public ResponseEntity<MemberResponse> assignMember(@PathVariable UUID id,
            @Valid @RequestBody AssignRoleRequest request) {
        MemberResponse member = organizationService.assignMember(id, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{userId}")
                .buildAndExpand(member.userId())
                .toUri();
        return ResponseEntity.created(location).body(member);
    }

    @DeleteMapping("/{id}/members/{userId}")
    @Operation(summary = "Remove a member from an organization")
    public ResponseEntity<Void> removeMember(@PathVariable UUID id,
            @PathVariable UUID userId) {
        organizationService.removeMember(id, userId);
        return ResponseEntity.noContent().build();
    }
}
