package com.mortenm12.authservice.service;

import com.mortenm12.authservice.api.dto.role.CreateRoleRequest;
import com.mortenm12.authservice.api.dto.role.RoleResponse;
import com.mortenm12.authservice.domain.Role;
import com.mortenm12.authservice.exception.ConflictException;
import com.mortenm12.authservice.exception.ResourceNotFoundException;
import com.mortenm12.authservice.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream().map(RoleResponse::from).toList();
    }

    public RoleResponse findById(UUID id) {
        return roleRepository.findById(id)
                .map(RoleResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
    }

    @Transactional
    public RoleResponse create(CreateRoleRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new ConflictException("Role already exists: " + request.name());
        }
        return RoleResponse.from(roleRepository.save(new Role(request.name(), request.description())));
    }

    @Transactional
    public void delete(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", id);
        }
        roleRepository.deleteById(id);
    }
}
