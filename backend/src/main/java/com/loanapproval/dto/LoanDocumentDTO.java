package com.loanapproval.dto;

import com.loanapproval.common.enums.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "LoanDocumentResponse", description = "Loan document details response")
public class LoanDocumentDTO {

    @Schema(description = "Document ID")
    private Long id;

    @Schema(description = "File name")
    private String fileName;

    @Schema(description = "File URL for download")
    private String fileUrl;

    @Schema(description = "File size in bytes")
    private Long fileSize;

    @Schema(description = "Document type")
    private DocumentType documentType;

    @Schema(description = "Document verification status")
    private boolean verified;

    @Schema(description = "Name of who verified the document")
    private String verifiedBy;

    @Schema(description = "Verification timestamp")
    private LocalDateTime verifiedAt;

    @Schema(description = "Extracted data from document (OCR)")
    private String extractedData;

    @Schema(description = "Extraction notes")
    private String extractionNotes;

    @Schema(description = "Upload timestamp")
    private LocalDateTime uploadedAt;

    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Record update timestamp")
    private LocalDateTime updatedAt;
}
