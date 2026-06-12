package com.mortenm12.authservice.service;

import com.mortenm12.authservice.api.dto.organization.CreateOrganizationRequest;
import com.mortenm12.authservice.api.dto.organization.OrganizationResponse;
import com.mortenm12.authservice.api.dto.organization.UpdateOrganizationRequest;
import com.mortenm12.authservice.api.dto.role.AssignRoleRequest;
import com.mortenm12.authservice.api.dto.role.MemberResponse;
import com.mortenm12.authservice.domain.Organization;
import com.mortenm12.authservice.exception.ConflictException;
import com.mortenm12.authservice.exception.ResourceNotFoundException;
import com.mortenm12.authservice.repository.OrganizationRepository;
import com.mortenm12.authservice.repository.RoleRepository;
import com.mortenm12.authservice.repository.UserOrganizationRoleRepository;
import com.mortenm12.authservice.repository.UserRepository;
import com.mortenm12.authservice.domain.UserOrganizationRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserOrganizationRoleRepository membershipRepository;

    public OrganizationService(OrganizationRepository organizationRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserOrganizationRoleRepository membershipRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.membershipRepository = membershipRepository;
    }

    public List<OrganizationResponse> findRoots() {
        return organizationRepository.findByParentIsNull()
                .stream()
                .map(OrganizationResponse::from)
                .toList();
    }

    public OrganizationResponse findById(UUID id) {
        return organizationRepository.findById(id)
                .map(OrganizationResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", id));
    }

    public List<OrganizationResponse> findChildren(UUID parentId) {
        if (!organizationRepository.existsById(parentId)) {
            throw new ResourceNotFoundException("Organization", parentId);
        }
        return organizationRepository.findByParentId(parentId)
                .stream()
                .map(OrganizationResponse::from)
                .toList();
    }

    @Transactional
    public OrganizationResponse create(CreateOrganizationRequest request) {
        if (organizationRepository.existsBySlug(request.slug())) {
            throw new ConflictException("Slug already in use: " + request.slug());
        }
        Organization parent = null;
        if (request.parentId() != null) {
            parent = organizationRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Organization", request.parentId()));
        }
        var org = new Organization(request.name(), request.slug(), parent);
        return OrganizationResponse.from(organizationRepository.save(org));
    }

    @Transactional
    public OrganizationResponse update(UUID id, UpdateOrganizationRequest request) {
        var org = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", id));
        org.update(request.name());
        return OrganizationResponse.from(org);
    }

    @Transactional
    public void delete(UUID id) {
        if (!organizationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Organization", id);
        }
        organizationRepository.deleteById(id);
    }

    public List<MemberResponse> findMembers(UUID orgId) {
        if (!organizationRepository.existsById(orgId)) {
            throw new ResourceNotFoundException("Organization", orgId);
        }
        return membershipRepository.findByOrganizationIdWithDetails(orgId)
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    @Transactional
    public MemberResponse assignMember(UUID orgId, AssignRoleRequest request) {
        var org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", orgId));
        var user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.userId()));
        var role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", request.roleId()));

        var existing = membershipRepository.findByUserIdAndOrganizationId(user.getId(), orgId);
        if (existing.isPresent()) {
            existing.get().updateRole(role);
            return MemberResponse.from(existing.get());
        }
        var membership = new UserOrganizationRole(user, org, role);
        return MemberResponse.from(membershipRepository.save(membership));
    }

    @Transactional
    public void removeMember(UUID orgId, UUID userId) {
        var membership = membershipRepository.findByUserIdAndOrganizationId(userId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Membership", "user " + userId + " in org " + orgId));
        membershipRepository.delete(membership);
    }
}
