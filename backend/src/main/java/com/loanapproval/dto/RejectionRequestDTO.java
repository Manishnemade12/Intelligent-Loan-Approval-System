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
@Schema(name = "RejectionRequest", description = "Request to reject a loan application")
public class RejectionRequestDTO {

    @Schema(description = "Rejection reason")
    @NotBlank(message = "Reason is required")
    private String reason;

    @Schema(description = "Additional notes")
    private String notes;
}
