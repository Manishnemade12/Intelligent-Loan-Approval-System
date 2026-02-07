package com.loanapproval.repository;

import com.loanapproval.entity.LoanDocument;
import com.loanapproval.common.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanDocumentRepository extends JpaRepository<LoanDocument, Long> {
    List<LoanDocument> findByLoanApplicationId(Long applicationId);
    List<LoanDocument> findByLoanApplicationIdAndDocumentType(Long applicationId, DocumentType documentType);
    List<LoanDocument> findByLoanApplicationIdAndVerifiedFalse(Long applicationId);
}
