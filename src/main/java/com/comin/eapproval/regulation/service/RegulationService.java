package com.comin.eapproval.regulation.service;

import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.regulation.RegulationErrorCode;
import com.comin.eapproval.regulation.dto.request.RegulationUploadRequest;
import com.comin.eapproval.regulation.dto.response.RegulationUploadResponse;
import com.comin.eapproval.regulation.entity.RegulationDocument;
import com.comin.eapproval.regulation.repository.RegulationDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

/** 규정 등록과 원본 파일 저장을 처리하는 서비스입니다. */
@Service
@RequiredArgsConstructor
public class RegulationService {

    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "hwp", "hwpx", "txt");

    private final RegulationDocumentRepository documentRepository;
    private final RegulationStorageService storageService;

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

        return new RegulationUploadResponse(
                document.getId(),
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
