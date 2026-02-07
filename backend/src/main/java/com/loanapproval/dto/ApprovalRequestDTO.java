package com.loanapproval.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ApprovalRequest", description = "Request to approve a loan application")
public class ApprovalRequestDTO {

    @Schema(description = "Approval notes")
    private String notes;
}
