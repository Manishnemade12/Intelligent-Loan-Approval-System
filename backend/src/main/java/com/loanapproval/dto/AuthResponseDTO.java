package com.loanapproval.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "AuthResponse", description = "Authentication response with JWT token")
public class AuthResponseDTO {

    @Schema(description = "JWT access token")
    private String token;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Token expiration time in milliseconds")
    private long expiresIn;

    @Schema(description = "Authenticated user information")
    private UserDTO user;
}
