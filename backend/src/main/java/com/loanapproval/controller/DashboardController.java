package com.loanapproval.controller;

import com.loanapproval.common.enums.UserRole;
import com.loanapproval.dto.DashboardStatsDTO;
import com.loanapproval.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard and analytics endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER', 'ADMIN')")
    @Operation(summary = "Get dashboard statistics", description = "Retrieve dashboard statistics")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats(Authentication authentication) {
        UserRole userRole = getUserRole(authentication);
        DashboardStatsDTO stats = dashboardService.getDashboardStats(userRole);
        return ResponseEntity.ok(stats);
    }

    private UserRole getUserRole(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority().replace("ROLE_", "");
            return UserRole.valueOf(role);
        }
        throw new IllegalArgumentException("Invalid user role");
    }
}
