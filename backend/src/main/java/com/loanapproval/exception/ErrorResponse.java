package com.loanapproval.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ErrorResponse", description = "API error response")
public class ErrorResponse {

    @Schema(description = "HTTP status code")
    private int status;

    @Schema(description = "Error message")
    private String message;

    @Schema(description = "Error timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "Request path")
    private String path;

    @Schema(description = "Validation errors (if applicable)")
    private Map<String, String> errors;

    @Schema(description = "Error trace ID for debugging")
    private String traceId;
}
