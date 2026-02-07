package com.loanapproval.service;

import com.loanapproval.common.enums.EmploymentType;
import com.loanapproval.dto.RiskFactorDTO;
import com.loanapproval.entity.LoanApplication;
import com.loanapproval.entity.RiskFactor;
import com.loanapproval.repository.RiskFactorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskScoringService {

    private final RiskFactorRepository riskFactorRepository;

    // Risk Factor Weights
    private static final BigDecimal CREDIT_SCORE_WEIGHT = new BigDecimal("0.30");
    private static final BigDecimal DTI_WEIGHT = new BigDecimal("0.25");
    private static final BigDecimal EMPLOYMENT_WEIGHT = new BigDecimal("0.20");
    private static final BigDecimal LTI_WEIGHT = new BigDecimal("0.15");
    private static final BigDecimal DOCUMENT_WEIGHT = new BigDecimal("0.10");

    // Thresholds
    private static final BigDecimal AUTO_APPROVE_THRESHOLD = new BigDecimal("30");
    private static final BigDecimal AUTO_REJECT_THRESHOLD = new BigDecimal("60");

    /**
     * Calculate overall risk score and individual risk factors
     */
    public Map<String, Object> calculateRiskScore(LoanApplication application, long verifiedDocCount) {
        List<RiskFactor> factors = new ArrayList<>();

        // Calculate individual factors
        RiskFactor creditScoreFactor = calculateCreditScoreFactor(application);
        factors.add(creditScoreFactor);

        RiskFactor dtiFactor = calculateDTIFactor(application);
        factors.add(dtiFactor);

        RiskFactor employmentFactor = calculateEmploymentFactor(application);
        factors.add(employmentFactor);

        RiskFactor ltiFactor = calculateLTIFactor(application);
        factors.add(ltiFactor);

        RiskFactor documentFactor = calculateDocumentVerificationFactor(application, verifiedDocCount);
        factors.add(documentFactor);

        // Save factors to database
        factors.forEach(f -> f.setLoanApplication(application));
        riskFactorRepository.saveAll(factors);

        // Calculate weighted overall score
        BigDecimal overallScore = calculateWeightedScore(factors);

        log.info("Risk score calculated for application {}: {}", application.getApplicationId(), overallScore);

        return Map.of(
                "riskScore", overallScore,
                "decision", getDecision(overallScore),
                "factors", factors
        );
    }

    /**
     * Credit Score Factor (30% weight)
     * Range: 300-850
     * Score formula: (creditScore - 300) / 5.5, capped at 100
     */
    private RiskFactor calculateCreditScoreFactor(LoanApplication application) {
        int creditScore = application.getCreditScore();

        // Normalize credit score: 300 -> 100, 850 -> 0
        BigDecimal normalizedScore = new BigDecimal(Math.max(0, Math.min(100, (creditScore - 300) * 100 / 550)))
                .setScale(2, RoundingMode.HALF_UP);

        String status = getStatus(creditScore, 720, 650); // Good: >=720, Critical: <650

        return RiskFactor.builder()
                .factorName("Credit Score")
                .description("Applicant's credit score - higher is better")
                .value(new BigDecimal(creditScore))
                .weight(CREDIT_SCORE_WEIGHT.multiply(new BigDecimal("100"))) // Convert to percentage
                .score(normalizedScore)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * DTI Factor (25% weight)
     * DTI = (Total Monthly Debt / Monthly Income) * 100
     * Score formula: 100 - (DTI * 2), capped at 0-100
     */
    private RiskFactor calculateDTIFactor(LoanApplication application) {
        BigDecimal monthlyIncome = application.getAnnualIncome().divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
        BigDecimal monthlyDebt = application.getExistingDebts().divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
        BigDecimal monthlyDebtNew = application.getLoanAmount().divide(new BigDecimal(application.getLoanTerm()), 2, RoundingMode.HALF_UP);

        BigDecimal totalMonthlyDebt = monthlyDebt.add(monthlyDebtNew);
        BigDecimal dtiRatio = monthlyIncome.compareTo(BigDecimal.ZERO) > 0 ?
                totalMonthlyDebt.divide(monthlyIncome, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) :
                new BigDecimal("100");

        // Score: higher DTI = higher risk = lower score
        BigDecimal score = new BigDecimal("100").subtract(dtiRatio.multiply(new BigDecimal("2")))
                .max(BigDecimal.ZERO)
                .min(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);

        // Store DTI ratio in application
        application.setDtiRatio(dtiRatio);

        String status = getStatus(dtiRatio.intValue(), 30, 43); // Good: <=30%, Critical: >43%

        return RiskFactor.builder()
                .factorName("Debt-to-Income Ratio")
                .description("Percentage of income needed to cover existing debts")
                .value(dtiRatio)
                .weight(DTI_WEIGHT.multiply(new BigDecimal("100")))
                .score(score)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Employment Stability Factor (20% weight)
     * Score: Years at job * 20%, capped at 100
     * Good: >=3 years, Critical: <1 year
     */
    private RiskFactor calculateEmploymentFactor(LoanApplication application) {
        int employmentDuration = application.getEmploymentDuration();

        // Score increases with employment duration
        BigDecimal score = new BigDecimal(Math.min(100, employmentDuration * 20))
                .setScale(2, RoundingMode.HALF_UP);

        String status = getStatus(employmentDuration, 3, 1); // Good: >=3, Critical: <1

        return RiskFactor.builder()
                .factorName("Employment Stability")
                .description("Years at current employment - longer is better")
                .value(new BigDecimal(employmentDuration))
                .weight(EMPLOYMENT_WEIGHT.multiply(new BigDecimal("100")))
                .score(score)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * LTI Factor (15% weight)
     * LTI = Loan Amount / Annual Income
     * Score formula: 100 - (LTI * 25), capped at 0-100
     * Good: <=3x, Critical: >5x
     */
    private RiskFactor calculateLTIFactor(LoanApplication application) {
        BigDecimal ltiRatio = application.getAnnualIncome().compareTo(BigDecimal.ZERO) > 0 ?
                application.getLoanAmount().divide(application.getAnnualIncome(), 2, RoundingMode.HALF_UP) :
                new BigDecimal("100");

        // Score: higher LTI = higher risk = lower score
        BigDecimal score = new BigDecimal("100").subtract(ltiRatio.multiply(new BigDecimal("25")))
                .max(BigDecimal.ZERO)
                .min(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);

        // Store LTI ratio in application
        application.setLtiRatio(ltiRatio);

        BigDecimal goodThreshold = new BigDecimal("3");
        BigDecimal criticalThreshold = new BigDecimal("5");

        String status = ltiRatio.compareTo(goodThreshold) <= 0 ? "GOOD" :
                ltiRatio.compareTo(criticalThreshold) > 0 ? "CRITICAL" : "WARNING";

        return RiskFactor.builder()
                .factorName("Loan-to-Income Ratio")
                .description("Loan amount relative to annual income - lower is better")
                .value(ltiRatio)
                .weight(LTI_WEIGHT.multiply(new BigDecimal("100")))
                .score(score)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Document Verification Factor (10% weight)
     * Score: (verified docs / total docs) * 100
     */
    private RiskFactor calculateDocumentVerificationFactor(LoanApplication application, long verifiedDocCount) {
        long totalDocCount = application.getDocuments().size();

        BigDecimal score = totalDocCount > 0 ?
                new BigDecimal(verifiedDocCount).divide(new BigDecimal(totalDocCount), 2, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")) :
                new BigDecimal("0");

        String status = verifiedDocCount == totalDocCount && totalDocCount > 0 ? "GOOD" :
                verifiedDocCount < totalDocCount / 2 ? "CRITICAL" : "WARNING";

        return RiskFactor.builder()
                .factorName("Document Verification")
                .description("Percentage of documents verified")
                .value(new BigDecimal(verifiedDocCount).divide(new BigDecimal(Math.max(1, totalDocCount)), 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")))
                .weight(DOCUMENT_WEIGHT.multiply(new BigDecimal("100")))
                .score(score)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Calculate weighted overall score from all factors
     */
    private BigDecimal calculateWeightedScore(List<RiskFactor> factors) {
        BigDecimal weightedSum = BigDecimal.ZERO;

        for (RiskFactor factor : factors) {
            BigDecimal contribution = factor.getScore()
                    .multiply(factor.getWeight())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            weightedSum = weightedSum.add(contribution);
        }

        return weightedSum.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Determine decision based on risk score
     */
    public String getDecision(BigDecimal riskScore) {
        if (riskScore.compareTo(AUTO_APPROVE_THRESHOLD) <= 0) {
            return "APPROVED";
        } else if (riskScore.compareTo(AUTO_REJECT_THRESHOLD) >= 0) {
            return "REJECTED";
        } else {
            return "MANUAL_REVIEW";
        }
    }

    /**
     * Helper to determine status
     */
    private String getStatus(int value, int goodThreshold, int criticalThreshold) {
        if (value >= goodThreshold) {
            return "GOOD";
        } else if (value <= criticalThreshold) {
            return "CRITICAL";
        } else {
            return "WARNING";
        }
    }

    private String getStatus(BigDecimal value, int goodThreshold, int criticalThreshold) {
        if (value.compareTo(new BigDecimal(goodThreshold)) >= 0) {
            return "GOOD";
        } else if (value.compareTo(new BigDecimal(criticalThreshold)) <= 0) {
            return "CRITICAL";
        } else {
            return "WARNING";
        }
    }

    /**
     * Get risk factors for an application
     */
    public List<RiskFactorDTO> getRiskFactors(Long applicationId) {
        return riskFactorRepository.findByLoanApplicationId(applicationId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    private RiskFactorDTO convertToDTO(RiskFactor factor) {
        return RiskFactorDTO.builder()
                .id(factor.getId())
                .factorName(factor.getFactorName())
                .description(factor.getDescription())
                .value(factor.getValue())
                .weight(factor.getWeight())
                .score(factor.getScore())
                .status(factor.getStatus())
                .createdAt(factor.getCreatedAt())
                .build();
    }
}
