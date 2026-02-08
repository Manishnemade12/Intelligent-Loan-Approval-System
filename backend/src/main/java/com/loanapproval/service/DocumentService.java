package com.loanapproval.service;

import com.loanapproval.common.enums.DocumentType;
import com.loanapproval.dto.LoanDocumentDTO;
import com.loanapproval.entity.LoanApplication;
import com.loanapproval.entity.LoanDocument;
import com.loanapproval.exception.ApplicationNotFoundException;
import com.loanapproval.exception.ValidationException;
import com.loanapproval.repository.LoanApplicationRepository;
import com.loanapproval.repository.LoanDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final LoanApplicationRepository applicationRepository;
    private final LoanDocumentRepository documentRepository;

    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "png", "jpg", "jpeg");
    private static final List<String> ALLOWED_MIMETYPES = Arrays.asList(
            "application/pdf",
            "image/png",
            "image/jpeg"
    );

    @Transactional
    public LoanDocumentDTO uploadDocument(Long applicationId, MultipartFile file, DocumentType documentType) {
        LoanApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        // Validate file
        validateFile(file);

        try {
            // Create upload directory if not exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save file
            String fileName = generateFileName(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            // Create document entity
            LoanDocument document = LoanDocument.builder()
                    .fileName(file.getOriginalFilename())
                    .fileUrl("/uploads/" + fileName)
                    .fileSize(file.getSize())
                    .documentType(documentType)
                    .verified(false)
                    .loanApplication(application)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            document = documentRepository.save(document);

            log.info("Document uploaded successfully for application {}", applicationId);

            return convertToDTO(document);

        } catch (IOException ex) {
            log.error("Error uploading document", ex);
            throw new ValidationException("Error uploading document: " + ex.getMessage());
        }
    }

    public byte[] downloadDocument(Long documentId) {
        LoanDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ApplicationNotFoundException("Document not found"));

        try {
            Path filePath = Paths.get(uploadDir, document.getFileUrl().replace("/uploads/", ""));
            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            log.error("Error downloading document", ex);
            throw new ValidationException("Error downloading document: " + ex.getMessage());
        }
    }

    @Transactional
    public void verifyDocument(Long documentId, boolean verified, String verifiedBy) {
        LoanDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ApplicationNotFoundException("Document not found"));

        document.setVerified(verified);
        document.setVerifiedBy(verifiedBy);
        document.setVerifiedAt(LocalDateTime.now());

        documentRepository.save(document);

        log.info("Document {} verified: {}", documentId, verified);
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        LoanDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ApplicationNotFoundException("Document not found"));

        // Delete file from disk
        try {
            Path filePath = Paths.get(uploadDir, document.getFileUrl().replace("/uploads/", ""));
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            log.warn("Error deleting file: {}", ex.getMessage());
        }

        documentRepository.delete(document);
        log.info("Document {} deleted", documentId);
    }

    public List<LoanDocumentDTO> getDocumentsByApplicationId(Long applicationId) {
        return documentRepository.findByLoanApplicationId(applicationId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new ValidationException("File size exceeds maximum allowed size of " + (maxFileSize / 1024 / 1024) + "MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new ValidationException("Invalid file name");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ValidationException("File type not allowed. Allowed types: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        if (!ALLOWED_MIMETYPES.contains(file.getContentType())) {
            throw new ValidationException("Invalid file MIME type");
        }
    }

    private String generateFileName(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }

    private LoanDocumentDTO convertToDTO(LoanDocument document) {
        return LoanDocumentDTO.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .fileSize(document.getFileSize())
                .documentType(document.getDocumentType())
                .verified(document.isVerified())
                .verifiedBy(document.getVerifiedBy())
                .verifiedAt(document.getVerifiedAt())
                .extractedData(document.getExtractedData())
                .extractionNotes(document.getExtractionNotes())
                .build();
    }
}
