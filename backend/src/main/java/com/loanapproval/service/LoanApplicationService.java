package com.loanapproval.service;

import com.loanapproval.common.enums.LoanStatus;
import com.loanapproval.dto.LoanApplicationRequestDTO;
import com.loanapproval.dto.LoanApplicationResponseDTO;
import com.loanapproval.entity.AuditLog;
import com.loanapproval.entity.LoanApplication;
import com.loanapproval.entity.LoanDocument;
import com.loanapproval.entity.User;
import com.loanapproval.exception.ApplicationNotFoundException;
import com.loanapproval.exception.UnauthorizedException;
import com.loanapproval.repository.AuditLogRepository;
import com.loanapproval.repository.LoanApplicationRepository;
import com.loanapproval.repository.LoanDocumentRepository;
import com.loanapproval.repository.UserRepository;
import com.loanapproval.common.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanApplicationRepository applicationRepository;
    private final LoanDocumentRepository documentRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final RiskScoringService riskScoringService;

    @Transactional
    public LoanApplicationResponseDTO createApplication(LoanApplicationRequestDTO requestDTO, String userEmail) {
        log.info("Creating loan application for email: {}", userEmail);

        LoanApplication application = LoanApplication.builder()
                .applicationId(generateApplicationId())
                .applicantName(requestDTO.getApplicantName())
                .email(requestDTO.getEmail())
                .phone(requestDTO.getPhone())
                .loanType(requestDTO.getLoanType())
                .loanAmount(requestDTO.getLoanAmount())
                .loanTerm(requestDTO.getLoanTerm())
                .purpose(requestDTO.getPurpose())
                .annualIncome(requestDTO.getAnnualIncome())
                .monthlyExpenses(requestDTO.getMonthlyExpenses())
                .creditScore(requestDTO.getCreditScore())
                .existingDebts(requestDTO.getExistingDebts())
                .employmentType(requestDTO.getEmploymentType())
                .employmentDuration(requestDTO.getEmploymentDuration())
                .employerName(requestDTO.getEmployerName())
                .status(LoanStatus.PENDING)
                .submittedAt(LocalDateTime.now())
                .build();

        application = applicationRepository.save(application);

        // Calculate risk score
        var riskScoreMap = riskScoringService.calculateRiskScore(application, 0);
        application.setRiskScore((java.math.BigDecimal) riskScoreMap.get("riskScore"));
        application = applicationRepository.save(application);

        // Log audit entry
        createAuditLog(application, "APPLICATION_CREATED", userEmail, "Loan application created");

        log.info("Application created successfully with ID: {}", application.getApplicationId());
        return convertToDTO(application);
    }

    public Page<LoanApplicationResponseDTO> getApplications(String userEmail, UserRole userRole, Pageable pageable) {
        Page<LoanApplication> applications;

        if (userRole == UserRole.CUSTOMER) {
            // Customers can only see their own applications
            applications = applicationRepository.findAll(pageable)
                    .filter(app -> app.getEmail().equals(userEmail));
        } else if (userRole == UserRole.OFFICER || userRole == UserRole.ADMIN) {
            // Officers and Admins can see all applications
            applications = applicationRepository.findAll(pageable);
        } else {
            throw new UnauthorizedException("Invalid user role");
        }

        return applications.map(this::convertToDTO);
    }

    public LoanApplicationResponseDTO getApplicationById(Long applicationId, String userEmail, UserRole userRole) {
        LoanApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with ID: " + applicationId));

        // Check authorization
        if (userRole == UserRole.CUSTOMER && !application.getEmail().equals(userEmail)) {
            throw new UnauthorizedException("You are not authorized to view this application");
        }

        return convertToDTO(application);
    }

    public LoanApplicationResponseDTO getApplicationByApplicationId(String applicationId, String userEmail, UserRole userRole) {
        LoanApplication application = applicationRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with ID: " + applicationId));

        // Check authorization
        if (userRole == UserRole.CUSTOMER && !application.getEmail().equals(userEmail)) {
            throw new UnauthorizedException("You are not authorized to view this application");
        }

        return convertToDTO(application);
    }

    @Transactional
    public LoanApplicationResponseDTO updateApplication(Long applicationId, LoanApplicationRequestDTO requestDTO, String userEmail) {
        LoanApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        // Only allow updates for pending applications
        if (application.getStatus() != LoanStatus.PENDING) {
            throw new UnauthorizedException("Cannot update application with status: " + application.getStatus());
        }

        application.setApplicantName(requestDTO.getApplicantName());
        application.setPhone(requestDTO.getPhone());
        application.setLoanType(requestDTO.getLoanType());
        application.setLoanAmount(requestDTO.getLoanAmount());
        application.setLoanTerm(requestDTO.getLoanTerm());
        application.setPurpose(requestDTO.getPurpose());
        application.setAnnualIncome(requestDTO.getAnnualIncome());
        application.setMonthlyExpenses(requestDTO.getMonthlyExpenses());
        application.setCreditScore(requestDTO.getCreditScore());
        application.setExistingDebts(requestDTO.getExistingDebts());
        application.setEmploymentType(requestDTO.getEmploymentType());
        application.setEmploymentDuration(requestDTO.getEmploymentDuration());
        application.setEmployerName(requestDTO.getEmployerName());

        application = applicationRepository.save(application);

        // Recalculate risk score
        var riskScoreMap = riskScoringService.calculateRiskScore(application, getVerifiedDocCount(applicationId));
        application.setRiskScore((java.math.BigDecimal) riskScoreMap.get("riskScore"));
        application = applicationRepository.save(application);

        createAuditLog(application, "APPLICATION_UPDATED", userEmail, "Application details updated");

        return convertToDTO(application);
    }

    private long getVerifiedDocCount(Long applicationId) {
        return documentRepository.findByLoanApplicationId(applicationId)
                .stream()
                .filter(LoanDocument::isVerified)
                .count();
    }

    @Transactional
    public void deleteApplication(Long applicationId, String userEmail) {
        LoanApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        applicationRepository.delete(application);
        createAuditLog(application, "APPLICATION_DELETED", userEmail, "Application deleted");
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

    private LoanApplicationResponseDTO convertToDTO(LoanApplication application) {
        return LoanApplicationResponseDTO.builder()
                .id(application.getId())
                .applicationId(application.getApplicationId())
                .applicantName(application.getApplicantName())
                .email(application.getEmail())
                .phone(application.getPhone())
                .loanType(application.getLoanType())
                .loanAmount(application.getLoanAmount())
                .loanTerm(application.getLoanTerm())
                .purpose(application.getPurpose())
                .annualIncome(application.getAnnualIncome())
                .monthlyExpenses(application.getMonthlyExpenses())
                .creditScore(application.getCreditScore())
                .existingDebts(application.getExistingDebts())
                .employmentType(application.getEmploymentType())
                .employmentDuration(application.getEmploymentDuration())
                .employerName(application.getEmployerName())
                .dtiRatio(application.getDtiRatio())
                .ltiRatio(application.getLtiRatio())
                .riskScore(application.getRiskScore())
                .status(application.getStatus())
                .submittedAt(application.getSubmittedAt())
                .reviewedAt(application.getReviewedAt())
                .reviewedBy(application.getReviewedBy())
                .assignedOfficer(application.getAssignedOfficer())
                .aiExplanation(application.getAiExplanation())
                .aiSuggestions(application.getAiSuggestions())
                .officerNotes(application.getOfficerNotes())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    private String generateApplicationId() {
        return "LA-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
