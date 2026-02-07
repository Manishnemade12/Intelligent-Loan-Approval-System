package com.loanapproval.controller;

import com.loanapproval.common.enums.UserRole;
import com.loanapproval.dto.LoanApplicationRequestDTO;
import com.loanapproval.dto.LoanApplicationResponseDTO;
import com.loanapproval.service.LoanApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@Tag(name = "Loan Applications", description = "Loan application management endpoints")
public class ApplicationController {

    private final LoanApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER', 'ADMIN')")
    @Operation(summary = "Submit new loan application", description = "Create a new loan application")
    public ResponseEntity<LoanApplicationResponseDTO> createApplication(
            @Valid @RequestBody LoanApplicationRequestDTO requestDTO,
            Authentication authentication) {
        LoanApplicationResponseDTO response = applicationService.createApplication(requestDTO, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER', 'ADMIN')")
    @Operation(summary = "Get loan applications", description = "Retrieve list of loan applications with pagination")
    public ResponseEntity<Page<LoanApplicationResponseDTO>> getApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            Authentication authentication) {
        
        UserRole userRole = getUserRole(authentication);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<LoanApplicationResponseDTO> applications = applicationService.getApplications(
                authentication.getName(), userRole, pageable);
        
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER', 'ADMIN')")
    @Operation(summary = "Get application details", description = "Retrieve detailed information about a specific application")
    public ResponseEntity<LoanApplicationResponseDTO> getApplicationById(
            @PathVariable Long id,
            Authentication authentication) {
        
        UserRole userRole = getUserRole(authentication);
        LoanApplicationResponseDTO application = applicationService.getApplicationById(id, authentication.getName(), userRole);
        
        return ResponseEntity.ok(application);
    }

    @GetMapping("/by-id/{applicationId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER', 'ADMIN')")
    @Operation(summary = "Get application by application ID", description = "Retrieve application details using application ID")
    public ResponseEntity<LoanApplicationResponseDTO> getApplicationByApplicationId(
            @PathVariable String applicationId,
            Authentication authentication) {
        
        UserRole userRole = getUserRole(authentication);
        LoanApplicationResponseDTO application = applicationService.getApplicationByApplicationId(
                applicationId, authentication.getName(), userRole);
        
        return ResponseEntity.ok(application);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER', 'ADMIN')")
    @Operation(summary = "Update application", description = "Update an existing loan application")
    public ResponseEntity<LoanApplicationResponseDTO> updateApplication(
            @PathVariable Long id,
            @Valid @RequestBody LoanApplicationRequestDTO requestDTO,
            Authentication authentication) {
        
        LoanApplicationResponseDTO response = applicationService.updateApplication(id, requestDTO, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER', 'ADMIN')")
    @Operation(summary = "Delete application", description = "Delete a loan application")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable Long id,
            Authentication authentication) {
        
        applicationService.deleteApplication(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    private UserRole getUserRole(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority().replace("ROLE_", "");
            return UserRole.valueOf(role);
        }
        throw new IllegalArgumentException("Invalid user role");
    }
}
