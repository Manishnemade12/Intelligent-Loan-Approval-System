package com.loanapproval.service;

import com.loanapproval.common.enums.LoanStatus;
import com.loanapproval.common.enums.UserRole;
import com.loanapproval.dto.DashboardStatsDTO;
import com.loanapproval.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final LoanApplicationRepository applicationRepository;

    public DashboardStatsDTO getDashboardStats(UserRole userRole) {
        long totalApplications = applicationRepository.countTotal();
        long pendingApplications = applicationRepository.countByStatus(LoanStatus.PENDING);
        long approvedApplications = applicationRepository.countByStatus(LoanStatus.APPROVED);
        long rejectedApplications = applicationRepository.countByStatus(LoanStatus.REJECTED);
        long manualReviewApplications = applicationRepository.countByStatus(LoanStatus.MANUAL_REVIEW);

        Double avgProcessingTime = applicationRepository.getAverageProcessingTimeInDays();
        if (avgProcessingTime == null) {
            avgProcessingTime = 0.0;
        }

        Double approvalRate = totalApplications > 0 ?
                (approvedApplications * 100.0) / totalApplications : 0.0;

        return DashboardStatsDTO.builder()
                .totalApplications(totalApplications)
                .pendingApplications(pendingApplications)
                .approvedApplications(approvedApplications)
                .rejectedApplications(rejectedApplications)
                .manualReviewApplications(manualReviewApplications)
                .avgProcessingTime(Math.round(avgProcessingTime * 100.0) / 100.0)
                .approvalRate(Math.round(approvalRate * 100.0) / 100.0)
                .build();
    }
}
