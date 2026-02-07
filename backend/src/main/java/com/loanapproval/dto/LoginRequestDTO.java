package com.loanapproval.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "LoginRequest", description = "User login request")
public class LoginRequestDTO {

    @Schema(description = "User email", example = "john@example.com")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "User password", example = "password123")
    @NotBlank(message = "Password is required")
    private String password;
}
