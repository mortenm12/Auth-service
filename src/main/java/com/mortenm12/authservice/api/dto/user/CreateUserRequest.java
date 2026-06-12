package com.mortenm12.authservice.api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @Email(message = "Must be a valid email")
        @NotBlank(message = "Email must not be blank")
        @Size(max = 255)
        String email,

        @NotBlank(message = "Username must not be blank")
        @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
        String username,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName
) { }
