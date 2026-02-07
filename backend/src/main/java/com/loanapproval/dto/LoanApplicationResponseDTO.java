package com.loanapproval.dto;

import com.loanapproval.common.enums.EmploymentType;
import com.loanapproval.common.enums.LoanStatus;
import com.loanapproval.common.enums.LoanType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "LoanApplicationResponse", description = "Loan application details response")
public class LoanApplicationResponseDTO {

    @Schema(description = "Database ID")
    private Long id;

    @Schema(description = "Unique application ID", example = "LA-2024-001")
    private String applicationId;

    @Schema(description = "Applicant full name")
    private String applicantName;

    @Schema(description = "Applicant email")
    private String email;

    @Schema(description = "Applicant phone number")
    private String phone;

    @Schema(description = "Type of loan")
    private LoanType loanType;

    @Schema(description = "Loan amount")
    private BigDecimal loanAmount;

    @Schema(description = "Loan tenure in months")
    private Integer loanTerm;

    @Schema(description = "Loan purpose")
    private String purpose;

    @Schema(description = "Annual income")
    private BigDecimal annualIncome;

    @Schema(description = "Monthly expenses")
    private BigDecimal monthlyExpenses;

    @Schema(description = "Credit score")
    private Integer creditScore;

    @Schema(description = "Existing debts")
    private BigDecimal existingDebts;

    @Schema(description = "Employment type")
    private EmploymentType employmentType;

    @Schema(description = "Years at current employment")
    private Integer employmentDuration;

    @Schema(description = "Employer name")
    private String employerName;

    @Schema(description = "Debt-to-Income ratio (%)")
    private BigDecimal dtiRatio;

    @Schema(description = "Loan-to-Income ratio")
    private BigDecimal ltiRatio;

    @Schema(description = "Risk score (0-100)")
    private BigDecimal riskScore;

    @Schema(description = "Application status")
    private LoanStatus status;

    @Schema(description = "Application submission timestamp")
    private LocalDateTime submittedAt;

    @Schema(description = "Application review timestamp")
    private LocalDateTime reviewedAt;

    @Schema(description = "Name of reviewer")
    private String reviewedBy;

    @Schema(description = "Assigned loan officer")
    private String assignedOfficer;

    @Schema(description = "AI-generated decision explanation")
    private String aiExplanation;

    @Schema(description = "AI-generated improvement suggestions")
    private String aiSuggestions;

    @Schema(description = "Officer notes")
    private String officerNotes;

    @Schema(description = "Associated documents")
    private List<LoanDocumentDTO> documents;

    @Schema(description = "Risk factors breakdown")
    private List<RiskFactorDTO> riskFactors;

    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Record update timestamp")
    private LocalDateTime updatedAt;
}
