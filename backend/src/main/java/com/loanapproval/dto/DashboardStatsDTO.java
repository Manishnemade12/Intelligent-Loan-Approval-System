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
@Schema(name = "DashboardStats", description = "Dashboard statistics")
public class DashboardStatsDTO {

    @Schema(description = "Total applications")
    private long totalApplications;

    @Schema(description = "Pending applications")
    private long pendingApplications;

    @Schema(description = "Approved applications")
    private long approvedApplications;

    @Schema(description = "Rejected applications")
    private long rejectedApplications;

    @Schema(description = "Manual review applications")
    private long manualReviewApplications;

    @Schema(description = "Average processing time in days")
    private Double avgProcessingTime;

    @Schema(description = "Approval rate percentage")
    private Double approvalRate;
}
