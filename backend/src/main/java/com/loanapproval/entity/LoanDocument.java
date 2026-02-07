package com.loanapproval.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loanapproval.common.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "loan_documents", indexes = {
    @Index(name = "idx_doc_app_id", columnList = "loan_application_id"),
    @Index(name = "idx_doc_type", columnList = "document_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean verified = false;

    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    // Extracted Data from OCR (stored as JSON)
    @Column(columnDefinition = "jsonb")
    private String extractedData;

    @Column(columnDefinition = "TEXT")
    private String extractionNotes;

    // Relations
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;

    // Audit Fields
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
