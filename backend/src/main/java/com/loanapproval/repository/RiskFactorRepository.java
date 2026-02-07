package com.loanapproval.repository;

import com.loanapproval.entity.RiskFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskFactorRepository extends JpaRepository<RiskFactor, Long> {
    List<RiskFactor> findByLoanApplicationId(Long applicationId);
    void deleteByLoanApplicationId(Long applicationId);
}
