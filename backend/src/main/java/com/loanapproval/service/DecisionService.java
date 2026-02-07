package com.loanapproval.service;

import com.loanapproval.common.enums.LoanStatus;
import com.loanapproval.dto.ApprovalRequestDTO;
import com.loanapproval.dto.ManualReviewRequestDTO;
import com.loanapproval.dto.RejectionRequestDTO;
import com.loanapproval.entity.AuditLog;
import com.loanapproval.entity.LoanApplication;
import com.loanapproval.exception.ApplicationNotFoundException;
import com.loanapproval.exception.UnauthorizedException;
import com.loanapproval.repository.AuditLogRepository;
import com.loanapproval.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecisionService {

    private final LoanApplicationRepository applicationRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void approveApplication(Long applicationId, ApprovalRequestDTO requestDTO, String approvedBy) {
        LoanApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        if (application.getStatus() != LoanStatus.PENDING && application.getStatus() != LoanStatus.MANUAL_REVIEW) {
            throw new UnauthorizedException("Cannot approve application with status: " + application.getStatus());
        }

        application.setStatus(LoanStatus.APPROVED);
        application.setReviewedAt(LocalDateTime.now());
        application.setReviewedBy(approvedBy);
        if (requestDTO.getNotes() != null) {
            application.setOfficerNotes(requestDTO.getNotes());
        }

        applicationRepository.save(application);

        createAuditLog(application, "APPLICATION_APPROVED", approvedBy,
                "Application approved. Notes: " + (requestDTO.getNotes() != null ? requestDTO.getNotes() : "None"));

        log.info("Application {} approved by {}", applicationId, approvedBy);
    }

    @Transactional
    public void rejectApplication(Long applicationId, RejectionRequestDTO requestDTO, String rejectedBy) {
        LoanApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        if (application.getStatus() != LoanStatus.PENDING && application.getStatus() != LoanStatus.MANUAL_REVIEW) {
            throw new UnauthorizedException("Cannot reject application with status: " + application.getStatus());
        }

        application.setStatus(LoanStatus.REJECTED);
        application.setReviewedAt(LocalDateTime.now());
        application.setReviewedBy(rejectedBy);
        if (requestDTO.getNotes() != null) {
            application.setOfficerNotes(requestDTO.getNotes());
        }

        applicationRepository.save(application);

        String auditNotes = String.format("Application rejected. Reason: %s. Additional notes: %s",
                requestDTO.getReason(),
                requestDTO.getNotes() != null ? requestDTO.getNotes() : "None");

        createAuditLog(application, "APPLICATION_REJECTED", rejectedBy, auditNotes);

        log.info("Application {} rejected by {} with reason: {}", applicationId, rejectedBy, requestDTO.getReason());
    }

    @Transactional
    public void requestManualReview(Long applicationId, ManualReviewRequestDTO requestDTO, String requestedBy) {
        LoanApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        if (application.getStatus() != LoanStatus.PENDING) {
            throw new UnauthorizedException("Cannot request manual review for application with status: " + application.getStatus());
        }

        application.setStatus(LoanStatus.MANUAL_REVIEW);
        if (requestDTO.getAssignToOfficer() != null) {
            application.setAssignedOfficer(requestDTO.getAssignToOfficer());
        }
        if (requestDTO.getNotes() != null) {
            application.setOfficerNotes(requestDTO.getNotes());
        }

        applicationRepository.save(application);

        String auditNotes = String.format("Manual review requested. Reason: %s. Assigned to: %s. Notes: %s",
                requestDTO.getReason(),
                requestDTO.getAssignToOfficer() != null ? requestDTO.getAssignToOfficer() : "Unassigned",
                requestDTO.getNotes() != null ? requestDTO.getNotes() : "None");

        createAuditLog(application, "MANUAL_REVIEW_REQUESTED", requestedBy, auditNotes);

        log.info("Manual review requested for application {} by {}", applicationId, requestedBy);
    }

    @Transactional
    public void addOfficerNotes(Long applicationId, String notes, String addedBy) {
        LoanApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        String existingNotes = application.getOfficerNotes() != null ? application.getOfficerNotes() : "";
        application.setOfficerNotes(existingNotes + "\n[" + LocalDateTime.now() + " by " + addedBy + "]: " + notes);

        applicationRepository.save(application);

        createAuditLog(application, "NOTES_ADDED", addedBy, "Officer notes added: " + notes);

        log.info("Notes added to application {} by {}", applicationId, addedBy);
    }

    private void createAuditLog(LoanApplication application, String action, String performedBy, String notes) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .performedBy(performedBy)
                .notes(notes)
                .loanApplication(application)
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(auditLog);
    }
}
