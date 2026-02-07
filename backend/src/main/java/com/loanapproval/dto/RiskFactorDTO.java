package com.loanapproval.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "RiskFactorResponse", description = "Risk factor details response")
public class RiskFactorDTO {

    @Schema(description = "Risk factor ID")
    private Long id;

    @Schema(description = "Factor name", example = "Credit Score")
    private String factorName;

    @Schema(description = "Factor description")
    private String description;

    @Schema(description = "Actual metric value")
    private BigDecimal value;

    @Schema(description = "Weight in overall score (%)")
    private BigDecimal weight;

    @Schema(description = "Weighted score contribution (0-100)")
    private BigDecimal score;

    @Schema(description = "Status indicator", example = "GOOD")
    private String status;

    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;
}
