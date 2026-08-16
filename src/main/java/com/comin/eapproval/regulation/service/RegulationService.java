package com.comin.eapproval.regulation.service;

import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.regulation.RegulationErrorCode;
import com.comin.eapproval.regulation.dto.request.RegulationUploadRequest;
import com.comin.eapproval.regulation.dto.response.RegulationUploadResponse;
import com.comin.eapproval.regulation.entity.RegulationDocument;
import com.comin.eapproval.regulation.entity.RegulationIngestionStatus;
import com.comin.eapproval.regulation.dto.response.RegulationAdminListResponse;
import com.comin.eapproval.global.response.PageResponse;
import com.comin.eapproval.regulation.entity.RegulationCategory;
import com.comin.eapproval.regulation.entity.RegulationStatus;
import com.comin.eapproval.regulation.repository.RegulationDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.util.Locale;
import java.util.List;
import java.util.Set;

/** 규정 등록과 원본 파일 저장을 처리하는 서비스입니다. */
@Service
@RequiredArgsConstructor
public class RegulationService {

    /** 관리자 규정 목록의 기본 페이지 크기입니다. */
    private static final int DEFAULT_PAGE_SIZE = 5;

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("md");

    private final RegulationDocumentRepository documentRepository;
    private final RegulationStorageService storageService;
    private final RegulationIngestionService ingestionService;
    private final RegulationQdrantService regulationQdrantService;
    private final RegulationIngestionSseService sseService;

    /**
     * 관리자 규정 목록을 검색 조건과 페이지 단위로 조회합니다.
     *
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param title 규정명 검색어
     * @param category 규정 분류
     * @param status 규정 사용 상태
     * @return 검색 조건에 맞는 규정 목록
     */
    @Transactional(readOnly = true)
    public PageResponse<RegulationAdminListResponse> getAdminRegulations(
            int page,
            int size,
            String title,
            RegulationCategory category,
            RegulationStatus status
    ) {
        int pageNumber = Math.max(page, 0);
        int pageSize = size > 0 ? Math.min(size, 50) : DEFAULT_PAGE_SIZE;
        return PageResponse.from(documentRepository.searchRegulations(
                title,
                category,
                status,
                PageRequest.of(pageNumber, pageSize)
        ).map(RegulationAdminListResponse::from));
    }

    /** 규정의 현재 AI 적재 상태를 조회합니다. */
    @Transactional(readOnly = true)
    public RegulationIngestionStatus getIngestionStatus(Long regulationId) {
        return documentRepository.findById(regulationId)
                .orElseThrow(() -> new BusinessException(RegulationErrorCode.REGULATION_UPLOAD_FAILED))
                .getIngestionStatus();
    }

    /** 관리자 규정 원본 파일을 S3에 저장하고 메타데이터를 등록합니다. */
    /**
     * 관리자가 규정 원본 파일을 S3에 저장하고 메타데이터를 등록합니다.
     *
     * @param request 규정 업로드 메타데이터
     * @param file 업로드할 규정 원본 파일
     * @return 업로드 결과
     */
    @Transactional
    public RegulationUploadResponse upload(
            RegulationUploadRequest request,
            MultipartFile file
    ) {
        validateFile(file);

        if (documentRepository.existsByTitleAndVersion(
                request.getTitle().trim(), request.getVersion().trim())) {
            throw new BusinessException(RegulationErrorCode.REGULATION_DUPLICATE_VERSION);
        }

        RegulationDocument document = documentRepository.saveAndFlush(
                RegulationDocument.create(
                        request.getTitle(),
                        request.getCategory(),
                        request.getVersion(),
                        request.getEffectiveDate(),
                        ""
                )
        );

        String objectKey;
        try {
            objectKey = storageService.upload(document.getId(), request.getVersion(), file);
        } catch (IOException | RuntimeException exception) {
            throw new BusinessException(RegulationErrorCode.REGULATION_UPLOAD_FAILED);
        }

        document.updateFileMetadata(
                file.getOriginalFilename(),
                objectKey,
                getExtension(file.getOriginalFilename()),
                file.getContentType(),
                file.getSize()
        );
        Long regulationId = document.getId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                ingestionService.ingestAsync(regulationId);
            }
        });

        return new RegulationUploadResponse(
                regulationId,
                document.getTitle(),
                document.getVersion(),
                document.getOriginalFilename(),
                document.getObjectKey()
        );
    }

    /**
     * 규정 원본 파일의 필수값·크기·확장자를 검증합니다.
     *
     * @param file 검증할 파일
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(RegulationErrorCode.REGULATION_FILE_REQUIRED);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(RegulationErrorCode.REGULATION_FILE_TOO_LARGE);
        }
        if (!ALLOWED_EXTENSIONS.contains(getExtension(file.getOriginalFilename()))) {
            throw new BusinessException(RegulationErrorCode.REGULATION_FILE_TYPE_NOT_ALLOWED);
        }
        validateMarkdown(file);
    }

    /** Markdown 규정 문서의 기본 구조를 검증합니다. */
    private void validateMarkdown(MultipartFile file) {
        try {
            String markdown = new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
            if (!markdown.matches("(?ms).*^#\\s+.+$.*")
                    || !markdown.matches("(?ms).*^##\\s+제\\d+조.*$.*")) {
                throw new BusinessException(RegulationErrorCode.REGULATION_MARKDOWN_INVALID);
            }
        } catch (IOException exception) {
            throw new BusinessException(RegulationErrorCode.REGULATION_UPLOAD_FAILED);
        }
    }

    /**
     * 관리자가 규정의 사용 상태를 변경합니다.
     *
     * @param regulationId 상태를 변경할 규정 ID
     * @param status 변경할 규정 사용 상태
     */
    @Transactional
    public void updateStatus(Long regulationId, RegulationStatus status) {
        RegulationDocument document = documentRepository.findById(regulationId)
                .orElseThrow(() -> new BusinessException(RegulationErrorCode.REGULATION_UPLOAD_FAILED));

        if (status == RegulationStatus.ACTIVE
                && document.getIngestionStatus() != RegulationIngestionStatus.COMPLETED) {
            throw new BusinessException(RegulationErrorCode.REGULATION_NOT_READY);
        }

        if (status == RegulationStatus.ACTIVE) {
            List<RegulationDocument> activeDocuments = documentRepository.findByTitleAndStatus(
                    document.getTitle(), RegulationStatus.ACTIVE);
            activeDocuments.stream()
                    .filter(activeDocument -> !activeDocument.getId().equals(regulationId))
                    .forEach(activeDocument -> {
                        regulationQdrantService.updateActiveStatus(activeDocument.getId(), false);
                        activeDocument.updateStatus(RegulationStatus.INACTIVE);
                    });
        }

        document.updateStatus(status);
        regulationQdrantService.updateActiveStatus(
                regulationId,
                status == RegulationStatus.ACTIVE
        );
    }

    /**
     * 규정의 AI 적재 처리 상태를 변경합니다.
     *
     * @param regulationId 상태를 변경할 규정 ID
     * @param status 변경할 AI 적재 처리 상태
     */
    @Transactional
    public void updateIngestionStatus(Long regulationId, RegulationIngestionStatus status) {
        documentRepository.findById(regulationId)
                .orElseThrow(() -> new BusinessException(RegulationErrorCode.REGULATION_UPLOAD_FAILED))
                .updateIngestionStatus(status);
    }

    /**
     * 파일명에서 확장자를 추출합니다.
     *
     * @param filename 원본 파일명
     * @return 소문자로 변환된 확장자
     */
    private String getExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
