package com.loanapproval.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_factors", indexes = {
    @Index(name = "idx_risk_app_id", columnList = "loan_application_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String factorName;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Actual metric value
    @Column(name = "metric_value", nullable = false)
    private BigDecimal value;

    // Weight in overall score (percentage)
    @Column(nullable = false)
    private BigDecimal weight;

    // Weighted score contribution (0-100)
    @Column(nullable = false)
    private BigDecimal score;

    // Status indicator
    @Column(nullable = false)
    private String status; // 'GOOD', 'WARNING', 'CRITICAL'

    // Relations
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;

    // Audit Fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
