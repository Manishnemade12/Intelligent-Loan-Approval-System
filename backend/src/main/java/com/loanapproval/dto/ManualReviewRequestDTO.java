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
@Schema(name = "ManualReviewRequest", description = "Request for manual review of a loan application")
public class ManualReviewRequestDTO {

    @Schema(description = "Review reason")
    @NotBlank(message = "Reason is required")
    private String reason;

    @Schema(description = "Assign to officer email")
    private String assignToOfficer;

    @Schema(description = "Additional notes")
    private String notes;
}
