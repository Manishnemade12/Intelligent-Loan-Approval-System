package com.loanapproval.dto;

import com.loanapproval.common.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "UserResponse", description = "User information response")
public class UserDTO {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "User name", example = "John Doe")
    private String name;

    @Schema(description = "User email", example = "john@example.com")
    private String email;

    @Schema(description = "User role", example = "CUSTOMER")
    private UserRole role;

    @Schema(description = "User avatar URL")
    private String avatarUrl;

    @Schema(description = "User active status", example = "true")
    private boolean active;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;
}
