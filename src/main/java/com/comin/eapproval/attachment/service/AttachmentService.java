package com.comin.eapproval.attachment.service;

import com.comin.eapproval.approval.repository.ApprovalRepository;
import com.comin.eapproval.attachment.dto.response.AttachmentListResponse;
import com.comin.eapproval.attachment.dto.response.AttachmentUploadResponse;
import com.comin.eapproval.attachment.dto.response.AttachmentUrlResponse;
import com.comin.eapproval.attachment.entity.Attachment;
import com.comin.eapproval.attachment.repository.AttachmentRepository;
import com.comin.eapproval.document.entity.Document;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.comin.eapproval.document.repository.DocumentRepository;
import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.attachment.AttachmentErrorCode;
import com.comin.eapproval.global.exception.document.DocumentErrorCode;
import com.comin.eapproval.member.entity.enums.MemberRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 문서 첨부파일 관련 비즈니스 로직을 처리하는 서비스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachmentService {

    private static final int MAX_ATTACHMENT_COUNT = 3;
    private static final long MAX_ATTACHMENT_SIZE = 10L * 1024 * 1024;
    private static final int MAX_FILENAME_LENGTH = 255;
    private static final ZoneId KST_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final Map<String, Set<String>> ALLOWED_FILE_TYPES = Map.of(
            "pdf", Set.of("application/pdf"),
            "jpg", Set.of("image/jpeg"),
            "jpeg", Set.of("image/jpeg"),
            "png", Set.of("image/png")
    );

    private final AttachmentRepository attachmentRepository;
    private final DocumentRepository documentRepository;
    private final ApprovalRepository approvalRepository;
    private final S3StorageService s3StorageService;

    /**
     * 임시저장 문서에 첨부파일을 업로드한다.
     *
     * @param documentId 문서 ID
     * @param memberId 로그인 회원 ID
     * @param file 업로드 파일
     * @return 첨부파일 업로드 결과
     */
    @Transactional
    public AttachmentUploadResponse uploadAttachment(
            Long documentId,
            Long memberId,
            MultipartFile file
    ) {
        Document document = findDocument(documentId);

        validateWriter(document, memberId);
        validateEditable(document);
        validateAttachmentCount(documentId);

        ValidatedFile validatedFile = validateFile(file);
        String objectKey = uploadToS3(
                documentId,
                validatedFile.extension(),
                validatedFile.contentType(),
                file
        );

        Attachment attachment = Attachment.create(
                document,
                validatedFile.originalFilename(),
                objectKey,
                validatedFile.contentType(),
                file.getSize()
        );

        try {
            Attachment savedAttachment = attachmentRepository.saveAndFlush(
                    attachment
            );
            return AttachmentUploadResponse.from(savedAttachment);
        } catch (RuntimeException exception) {
            deleteUploadedObject(objectKey);
            throw exception;
        }
    }

    /**
     * 문서에 포함된 첨부파일 목록을 조회한다.
     *
     * @param documentId 문서 ID
     * @param memberId 로그인 회원 ID
     * @param role 로그인 회원 권한
     * @return 첨부파일 목록
     */
    public List<AttachmentListResponse> getAttachments(
            Long documentId,
            Long memberId,
            MemberRole role
    ) {
        Document document = findDocument(documentId);

        validateReadPermission(document, memberId, role);

        return attachmentRepository
                .findAllByDocumentIdAndDeletedAtIsNullOrderByCreatedAtAsc(
                        documentId
                )
                .stream()
                .map(AttachmentListResponse::from)
                .toList();
    }

    /**
     * 임시저장 문서에서 첨부파일을 삭제한다.
     *
     * @param documentId 문서 ID
     * @param attachmentId 첨부파일 ID
     * @param memberId 로그인 회원 ID
     */
    @Transactional
    public void deleteAttachment(
            Long documentId,
            Long attachmentId,
            Long memberId
    ) {
        Document document = findDocument(documentId);

        validateWriter(document, memberId);
        validateEditable(document);

        Attachment attachment = attachmentRepository
                .findByIdAndDocumentIdAndDeletedAtIsNull(
                        attachmentId,
                        documentId
                )
                .orElseThrow(() -> new BusinessException(
                        AttachmentErrorCode.ATTACHMENT_NOT_FOUND
                ));

        attachment.delete();
        s3StorageService.delete(attachment.getObjectKey());
    }

    /**
     * 첨부파일을 브라우저에서 미리 볼 수 있는 URL을 발급한다.
     *
     * @param documentId 문서 ID
     * @param attachmentId 첨부파일 ID
     * @param memberId 로그인 회원 ID
     * @param role 로그인 회원 권한
     * @return 첨부파일 미리보기 URL 정보
     */
    public AttachmentUrlResponse createPreviewUrl(
            Long documentId,
            Long attachmentId,
            Long memberId,
            MemberRole role
    ) {
        Attachment attachment = findReadableAttachment(
                documentId,
                attachmentId,
                memberId,
                role
        );

        S3StorageService.PresignedUrl presignedUrl =
                s3StorageService.createPreviewUrl(
                        attachment.getObjectKey(),
                        attachment.getOriginalFilename(),
                        attachment.getContentType()
                );

        return toUrlResponse(presignedUrl);
    }

    /**
     * 첨부파일을 원본 파일명으로 내려받을 수 있는 URL을 발급한다.
     *
     * @param documentId 문서 ID
     * @param attachmentId 첨부파일 ID
     * @param memberId 로그인 회원 ID
     * @param role 로그인 회원 권한
     * @return 첨부파일 다운로드 URL 정보
     */
    public AttachmentUrlResponse createDownloadUrl(
            Long documentId,
            Long attachmentId,
            Long memberId,
            MemberRole role
    ) {
        Attachment attachment = findReadableAttachment(
                documentId,
                attachmentId,
                memberId,
                role
        );

        S3StorageService.PresignedUrl presignedUrl =
                s3StorageService.createDownloadUrl(
                        attachment.getObjectKey(),
                        attachment.getOriginalFilename(),
                        attachment.getContentType()
                );

        return toUrlResponse(presignedUrl);
    }

    /**
     * 파일 내용과 메타데이터를 검증한다.
     *
     * @param file 업로드 파일
     * @return 검증된 파일 정보
     */
    private ValidatedFile validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(
                    AttachmentErrorCode.EMPTY_ATTACHMENT
            );
        }

        if (file.getSize() > MAX_ATTACHMENT_SIZE) {
            throw new BusinessException(
                    AttachmentErrorCode.ATTACHMENT_SIZE_EXCEEDED
            );
        }

        String cleanedFilename = StringUtils.cleanPath(
                file.getOriginalFilename() == null
                        ? ""
                        : file.getOriginalFilename()
        );

        if (!StringUtils.hasText(cleanedFilename)
                || cleanedFilename.contains("..")) {
            throw new BusinessException(
                    AttachmentErrorCode.INVALID_ATTACHMENT_FILENAME
            );
        }

        String originalFilename = StringUtils.getFilename(cleanedFilename);

        if (!StringUtils.hasText(originalFilename)) {
            throw new BusinessException(
                    AttachmentErrorCode.INVALID_ATTACHMENT_FILENAME
            );
        }

        if (originalFilename.length() > MAX_FILENAME_LENGTH) {
            throw new BusinessException(
                    AttachmentErrorCode.ATTACHMENT_FILENAME_TOO_LONG
            );
        }

        String extension = StringUtils.getFilenameExtension(originalFilename);
        String contentType = file.getContentType();

        if (!StringUtils.hasText(extension)
                || !StringUtils.hasText(contentType)) {
            throw new BusinessException(
                    AttachmentErrorCode.INVALID_ATTACHMENT_TYPE
            );
        }

        String normalizedExtension = extension.toLowerCase(Locale.ROOT);
        String normalizedContentType = contentType.toLowerCase(Locale.ROOT);
        Set<String> allowedContentTypes = ALLOWED_FILE_TYPES.get(
                normalizedExtension
        );

        if (allowedContentTypes == null
                || !allowedContentTypes.contains(normalizedContentType)) {
            throw new BusinessException(
                    AttachmentErrorCode.INVALID_ATTACHMENT_TYPE
            );
        }

        return new ValidatedFile(
                originalFilename,
                normalizedExtension,
                normalizedContentType
        );
    }

    /**
     * 검증된 파일을 S3에 업로드한다.
     */
    private String uploadToS3(
            Long documentId,
            String extension,
            String contentType,
            MultipartFile file
    ) {
        try (InputStream inputStream = file.getInputStream()) {
            return s3StorageService.upload(
                    documentId,
                    extension,
                    contentType,
                    file.getSize(),
                    inputStream
            );
        } catch (IOException exception) {
            log.error(
                    "첨부파일 입력 스트림을 읽지 못했습니다. documentId={}",
                    documentId,
                    exception
            );
            throw new BusinessException(
                    AttachmentErrorCode.ATTACHMENT_UPLOAD_FAILED
            );
        }
    }

    /**
     * DB 저장 실패 시 이미 업로드된 S3 객체를 정리한다.
     */
    private void deleteUploadedObject(String objectKey) {
        try {
            s3StorageService.delete(objectKey);
        } catch (RuntimeException cleanupException) {
            log.error(
                    "DB 저장 실패 후 S3 객체 정리에 실패했습니다. objectKey={}",
                    objectKey,
                    cleanupException
            );
        }
    }

    /**
     * 삭제되지 않은 문서를 조회한다.
     */
    private Document findDocument(Long documentId) {
        return documentRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new BusinessException(
                        DocumentErrorCode.DOCUMENT_NOT_FOUND
                ));
    }

    /**
     * 조회 권한을 확인하고 문서에 속한 첨부파일을 조회한다.
     */
    private Attachment findReadableAttachment(
            Long documentId,
            Long attachmentId,
            Long memberId,
            MemberRole role
    ) {
        Document document = findDocument(documentId);
        validateReadPermission(document, memberId, role);

        return attachmentRepository
                .findByIdAndDocumentIdAndDeletedAtIsNull(
                        attachmentId,
                        documentId
                )
                .orElseThrow(() -> new BusinessException(
                        AttachmentErrorCode.ATTACHMENT_NOT_FOUND
                ));
    }

    /**
     * Presigned URL 만료 시각을 한국 표준시 응답으로 변환한다.
     */
    private AttachmentUrlResponse toUrlResponse(
            S3StorageService.PresignedUrl presignedUrl
    ) {
        LocalDateTime expiresAt = LocalDateTime.ofInstant(
                presignedUrl.expiresAt(),
                KST_ZONE_ID
        );

        return AttachmentUrlResponse.of(
                presignedUrl.url(),
                expiresAt
        );
    }

    /**
     * 로그인 사용자가 문서 작성자인지 검증한다.
     */
    private void validateWriter(Document document, Long memberId) {
        if (!document.getWriter().getId().equals(memberId)) {
            throw new BusinessException(
                    DocumentErrorCode.DOCUMENT_UPDATE_DENIED
            );
        }
    }

    /**
     * 문서가 첨부파일을 변경할 수 있는 상태인지 검증한다.
     */
    private void validateEditable(Document document) {
        if (document.getStatus() != DocumentStatus.DRAFT) {
            throw new BusinessException(
                    DocumentErrorCode.DOCUMENT_NOT_EDITABLE
            );
        }
    }

    /**
     * 문서의 첨부파일 개수가 최대 허용 개수를 넘지 않는지 검증한다.
     */
    private void validateAttachmentCount(Long documentId) {
        long attachmentCount = attachmentRepository
                .countByDocumentIdAndDeletedAtIsNull(documentId);

        if (attachmentCount >= MAX_ATTACHMENT_COUNT) {
            throw new BusinessException(
                    AttachmentErrorCode.ATTACHMENT_COUNT_EXCEEDED
            );
        }
    }

    /**
     * 로그인 사용자가 문서와 첨부파일을 조회할 수 있는지 검증한다.
     */
    private void validateReadPermission(
            Document document,
            Long memberId,
            MemberRole role
    ) {
        boolean isWriter = document.getWriter().getId().equals(memberId);
        boolean isApprover = approvalRepository
                .existsByDocumentIdAndApproverId(document.getId(), memberId);
        boolean isAdmin = role == MemberRole.ROLE_ADMIN;

        if (!isWriter && !isApprover && !isAdmin) {
            throw new BusinessException(
                    DocumentErrorCode.DOCUMENT_ACCESS_DENIED
            );
        }
    }

    /**
     * 검증이 완료된 첨부파일 메타데이터.
     */
    private record ValidatedFile(
            String originalFilename,
            String extension,
            String contentType
    ) {
    }
}
