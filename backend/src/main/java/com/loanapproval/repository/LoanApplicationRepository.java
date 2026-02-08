package com.loanapproval.repository;

import com.loanapproval.entity.LoanApplication;
import com.loanapproval.common.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    Optional<LoanApplication> findByApplicationId(String applicationId);
    List<LoanApplication> findByEmail(String email);
    Page<LoanApplication> findByEmail(String email, Pageable pageable);
    Page<LoanApplication> findByStatus(LoanStatus status, Pageable pageable);
    
    @Query("SELECT la FROM LoanApplication la WHERE la.email = :email ORDER BY la.submittedAt DESC")
    List<LoanApplication> findByEmailOrderBySubmittedAtDesc(@Param("email") String email);

    @Query("SELECT COUNT(la) FROM LoanApplication la WHERE la.status = :status")
    long countByStatus(@Param("status") LoanStatus status);

    @Query("SELECT COUNT(la) FROM LoanApplication la")
    long countTotal();

    @Query("SELECT AVG(EXTRACT(DAY FROM (la.reviewedAt - la.submittedAt))) FROM LoanApplication la WHERE la.reviewedAt IS NOT NULL")
    Double getAverageProcessingTimeInDays();

    @Query("SELECT COUNT(la) FROM LoanApplication la WHERE la.status = 'APPROVED' AND la.submittedAt >= :startDate")
    long countApprovedSince(@Param("startDate") LocalDateTime startDate);

    Page<LoanApplication> findAll(Pageable pageable);
}
