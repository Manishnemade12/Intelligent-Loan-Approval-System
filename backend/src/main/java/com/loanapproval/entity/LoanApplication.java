package com.loanapproval.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loanapproval.common.enums.DocumentType;
import com.loanapproval.common.enums.LoanStatus;
import com.loanapproval.common.enums.LoanType;
import com.loanapproval.common.enums.EmploymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_applications", indexes = {
    @Index(name = "idx_app_id", columnList = "application_id", unique = true),
    @Index(name = "idx_applicant_email", columnList = "email"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_submitted_at", columnList = "submitted_at"),
    @Index(name = "idx_applicant_id", columnList = "applicant_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false, unique = true)
    private String applicationId;

    // Personal Information
    @Column(nullable = false)
    private String applicantName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    // Loan Details
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    @Column(nullable = false)
    private BigDecimal loanAmount;

    @Column(nullable = false)
    private Integer loanTerm; // in months

    @Column(nullable = false)
    private String purpose;

    // Financial Profile
    @Column(nullable = false)
    private BigDecimal annualIncome;

    @Column(nullable = false)
    private BigDecimal monthlyExpenses;

    @Column(nullable = false)
    private Integer creditScore; // 300-850

    @Column(nullable = false)
    private BigDecimal existingDebts;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType;

    @Column(nullable = false)
    private Integer employmentDuration; // in years

    @Column
    private String employerName;

    // Calculated Metrics
    @Column(name = "dti_ratio", columnDefinition = "DECIMAL(5,2)")
    private BigDecimal dtiRatio; // Debt-to-Income Ratio

    @Column(name = "lti_ratio", columnDefinition = "DECIMAL(5,2)")
    private BigDecimal ltiRatio; // Loan-to-Income Ratio

    @Column(name = "risk_score", columnDefinition = "DECIMAL(5,2)")
    private BigDecimal riskScore; // 0-100

    // Status & Workflow
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.PENDING;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "assigned_officer")
    private String assignedOfficer;

    // AI Analysis
    @Column(columnDefinition = "TEXT")
    private String aiExplanation;

    @Column(columnDefinition = "TEXT")
    private String aiSuggestions; // JSON array as string

    @Column(name = "ai_analyzed_at")
    private LocalDateTime aiAnalyzedAt;

    // Officer Notes
    @Column(columnDefinition = "TEXT")
    private String officerNotes;

    // Relations
    @JsonIgnore
    @OneToMany(mappedBy = "loanApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LoanDocument> documents = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "loanApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AuditLog> auditLogs = new ArrayList<>();

    // Audit Fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
