package com.loanapproval.controller;

import com.loanapproval.dto.ApprovalRequestDTO;
import com.loanapproval.dto.ManualReviewRequestDTO;
import com.loanapproval.dto.RejectionRequestDTO;
import com.loanapproval.service.DecisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@Tag(name = "Application Decisions", description = "Application approval/rejection/review endpoints")
public class DecisionController {

    private final DecisionService decisionService;

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    @Operation(summary = "Approve application", description = "Approve a loan application")
    public ResponseEntity<Void> approveApplication(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalRequestDTO requestDTO,
            Authentication authentication) {
        
        decisionService.approveApplication(id, requestDTO != null ? requestDTO : new ApprovalRequestDTO(), authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    @Operation(summary = "Reject application", description = "Reject a loan application with reason")
    public ResponseEntity<Void> rejectApplication(
            @PathVariable Long id,
            @Valid @RequestBody RejectionRequestDTO requestDTO,
            Authentication authentication) {
        
        decisionService.rejectApplication(id, requestDTO, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/review-request")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    @Operation(summary = "Request manual review", description = "Request manual review of a loan application")
    public ResponseEntity<Void> requestManualReview(
            @PathVariable Long id,
            @Valid @RequestBody ManualReviewRequestDTO requestDTO,
            Authentication authentication) {
        
        decisionService.requestManualReview(id, requestDTO, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/notes")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    @Operation(summary = "Add officer notes", description = "Add officer notes to an application")
    public ResponseEntity<Void> addOfficerNotes(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        
        String notes = request.get("notes");
        decisionService.addOfficerNotes(id, notes, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/audit-log")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    @Operation(summary = "Get audit log", description = "Get audit log for an application")
    public ResponseEntity<Void> getAuditLog(@PathVariable Long id) {
        // Will be implemented in future
        return ResponseEntity.ok().build();
    }
}

// Needed for request body
import java.util.Map;
