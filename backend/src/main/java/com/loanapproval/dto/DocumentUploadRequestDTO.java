package com.loanapproval.dto;

import com.loanapproval.common.enums.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "DocumentUploadRequest", description = "Document upload request")
public class DocumentUploadRequestDTO {

    @Schema(description = "Application ID")
    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @Schema(description = "Document type")
    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @Schema(description = "Document description")
    private String description;
}
