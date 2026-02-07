package com.loanapproval.dto;

import com.loanapproval.common.enums.EmploymentType;
import com.loanapproval.common.enums.LoanType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "LoanApplicationRequest", description = "Request to submit a new loan application")
public class LoanApplicationRequestDTO {

    @Schema(description = "Applicant full name", example = "John Doe")
    @NotBlank(message = "Applicant name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String applicantName;

    @Schema(description = "Applicant email", example = "john@example.com")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "Applicant phone number", example = "9876543210")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;

    @Schema(description = "Type of loan", example = "PERSONAL")
    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @Schema(description = "Requested loan amount", example = "50000")
    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "1000", message = "Loan amount must be at least 1000")
    private BigDecimal loanAmount;

    @Schema(description = "Loan tenure in months", example = "24")
    @NotNull(message = "Loan term is required")
    @Min(value = 12, message = "Loan term must be at least 12 months")
    private Integer loanTerm;

    @Schema(description = "Loan purpose", example = "Home renovation")
    @NotBlank(message = "Loan purpose is required")
    @Size(min = 10, max = 500, message = "Purpose must be between 10 and 500 characters")
    private String purpose;

    @Schema(description = "Annual income", example = "600000")
    @NotNull(message = "Annual income is required")
    @DecimalMin(value = "0", message = "Annual income must be positive")
    private BigDecimal annualIncome;

    @Schema(description = "Monthly expenses", example = "30000")
    @NotNull(message = "Monthly expenses is required")
    @DecimalMin(value = "0", message = "Monthly expenses cannot be negative")
    private BigDecimal monthlyExpenses;

    @Schema(description = "Credit score (300-850)", example = "750")
    @NotNull(message = "Credit score is required")
    @Min(value = 300, message = "Credit score must be at least 300")
    @Max(value = 850, message = "Credit score cannot exceed 850")
    private Integer creditScore;

    @Schema(description = "Existing debts", example = "100000")
    @NotNull(message = "Existing debts is required")
    @DecimalMin(value = "0", message = "Existing debts cannot be negative")
    private BigDecimal existingDebts;

    @Schema(description = "Employment type", example = "SALARIED")
    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @Schema(description = "Years at current employment", example = "5")
    @NotNull(message = "Employment duration is required")
    @Min(value = 0, message = "Employment duration cannot be negative")
    private Integer employmentDuration;

    @Schema(description = "Employer name")
    private String employerName;
}
