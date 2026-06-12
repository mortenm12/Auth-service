package com.mortenm12.authservice.service;

import com.mortenm12.authservice.api.dto.user.CreateUserRequest;
import com.mortenm12.authservice.api.dto.user.UpdateUserRequest;
import com.mortenm12.authservice.api.dto.user.UserResponse;
import com.mortenm12.authservice.domain.User;
import com.mortenm12.authservice.exception.ConflictException;
import com.mortenm12.authservice.exception.ResourceNotFoundException;
import com.mortenm12.authservice.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .disabled(!user.isEnabled())
                .authorities(List.of())
                .build();
    }

    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::from);
    }

    public UserResponse findById(UUID id) {
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already in use: " + request.email());
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already taken: " + request.username());
        }
        var user = new User(
                request.email(),
                request.username(),
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName()
        );
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already in use: " + request.email());
        }
        user.update(request.email(), request.firstName(), request.lastName());
        return UserResponse.from(user);
    }

    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }
}
