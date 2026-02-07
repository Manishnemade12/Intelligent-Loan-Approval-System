package com.loanapproval.controller;

import com.loanapproval.common.enums.DocumentType;
import com.loanapproval.dto.DocumentUploadRequestDTO;
import com.loanapproval.dto.LoanDocumentDTO;
import com.loanapproval.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "Document upload and management endpoints")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER', 'ADMIN')")
    @Operation(summary = "Upload document", description = "Upload a document for a loan application")
    public ResponseEntity<LoanDocumentDTO> uploadDocument(
            @RequestParam Long applicationId,
            @RequestParam DocumentType documentType,
            @RequestParam MultipartFile file,
            Authentication authentication) {

        LoanDocumentDTO document = documentService.uploadDocument(applicationId, file, documentType);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER', 'ADMIN')")
    @Operation(summary = "Download document", description = "Download a specific document")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        byte[] fileContent = documentService.downloadDocument(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document")
                .body(fileContent);
    }

    @PostMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    @Operation(summary = "Verify document", description = "Verify or unverify a document")
    public ResponseEntity<Void> verifyDocument(
            @PathVariable Long id,
            @RequestParam boolean verified,
            Authentication authentication) {

        documentService.verifyDocument(id, verified, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER', 'ADMIN')")
    @Operation(summary = "Delete document", description = "Delete a document")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OFFICER', 'ADMIN')")
    @Operation(summary = "Get application documents", description = "Get all documents for an application")
    public ResponseEntity<List<LoanDocumentDTO>> getDocumentsByApplicationId(@PathVariable Long applicationId) {
        List<LoanDocumentDTO> documents = documentService.getDocumentsByApplicationId(applicationId);
        return ResponseEntity.ok(documents);
    }
}
