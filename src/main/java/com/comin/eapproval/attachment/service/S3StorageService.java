package com.comin.eapproval.attachment.service;

import com.comin.eapproval.global.config.properties.S3Properties;
import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.attachment.AttachmentErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

/**
 * S3에 첨부파일 객체를 저장하고 삭제하는 서비스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService {

    private static final String ATTACHMENT_KEY_PREFIX = "documents";
    private static final Duration PRESIGNED_URL_DURATION =
            Duration.ofMinutes(5);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    /**
     * 첨부파일을 S3에 업로드하고 생성된 객체 키를 반환한다.
     *
     * @param documentId 문서 ID
     * @param extension 검증된 파일 확장자
     * @param contentType 파일 MIME 타입
     * @param fileSize 파일 크기
     * @param inputStream 파일 입력 스트림
     * @return S3 객체 키
     */
    public String upload(
            Long documentId,
            String extension,
            String contentType,
            long fileSize,
            InputStream inputStream
    ) {
        String objectKey = createObjectKey(documentId, extension);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(getAttachmentBucket())
                .key(objectKey)
                .contentType(contentType)
                .contentLength(fileSize)
                .build();

        try {
            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(inputStream, fileSize)
            );

            return objectKey;
        } catch (SdkException exception) {
            log.error(
                    "S3 첨부파일 업로드에 실패했습니다. documentId={}, objectKey={}",
                    documentId,
                    objectKey,
                    exception
            );
            throw new BusinessException(
                    AttachmentErrorCode.ATTACHMENT_UPLOAD_FAILED
            );
        }
    }

    /**
     * S3에서 첨부파일 객체를 삭제한다.
     *
     * @param objectKey 삭제할 S3 객체 키
     */
    public void delete(String objectKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(getAttachmentBucket())
                .key(objectKey)
                .build();

        try {
            s3Client.deleteObject(request);
        } catch (SdkException exception) {
            log.error(
                    "S3 첨부파일 삭제에 실패했습니다. objectKey={}",
                    objectKey,
                    exception
            );
            throw new BusinessException(
                    AttachmentErrorCode.ATTACHMENT_DELETE_FAILED
            );
        }
    }

    /**
     * 기존 첨부파일을 새 문서 경로에 새로운 객체로 복사한다.
     *
     * @param targetDocumentId 복사 대상 문서 ID
     * @param sourceObjectKey 원본 S3 객체 키
     * @param extension 검증된 파일 확장자
     * @return 새로 생성된 S3 객체 키
     */
    public String copy(
            Long targetDocumentId,
            String sourceObjectKey,
            String extension
    ) {
        String targetObjectKey = createObjectKey(
                targetDocumentId,
                extension
        );
        CopyObjectRequest request = CopyObjectRequest.builder()
                .sourceBucket(getAttachmentBucket())
                .sourceKey(sourceObjectKey)
                .destinationBucket(getAttachmentBucket())
                .destinationKey(targetObjectKey)
                .build();

        try {
            s3Client.copyObject(request);
            return targetObjectKey;
        } catch (SdkException exception) {
            log.error(
                    "S3 첨부파일 복사에 실패했습니다. sourceObjectKey={}, targetObjectKey={}",
                    sourceObjectKey,
                    targetObjectKey,
                    exception
            );
            throw new BusinessException(
                    AttachmentErrorCode.ATTACHMENT_COPY_FAILED
            );
        }
    }

    /**
     * 브라우저에서 첨부파일을 바로 표시할 수 있는 URL을 생성한다.
     *
     * @param objectKey S3 객체 키
     * @param originalFilename 원본 파일명
     * @param contentType 파일 MIME 타입
     * @return Presigned URL 정보
     */
    public PresignedUrl createPreviewUrl(
            String objectKey,
            String originalFilename,
            String contentType
    ) {
        String contentDisposition = "inline; filename*=UTF-8''"
                + encodeFilename(originalFilename);

        return createPresignedUrl(
                objectKey,
                contentType,
                contentDisposition
        );
    }

    /**
     * 첨부파일을 원본 파일명으로 내려받을 수 있는 URL을 생성한다.
     *
     * @param objectKey S3 객체 키
     * @param originalFilename 원본 파일명
     * @param contentType 파일 MIME 타입
     * @return Presigned URL 정보
     */
    public PresignedUrl createDownloadUrl(
            String objectKey,
            String originalFilename,
            String contentType
    ) {
        String contentDisposition = "attachment; filename=\"attachment\"; "
                + "filename*=UTF-8''"
                + encodeFilename(originalFilename);

        return createPresignedUrl(
                objectKey,
                contentType,
                contentDisposition
        );
    }

    /**
     * 지정한 응답 형식으로 S3 Presigned URL을 생성한다.
     */
    private PresignedUrl createPresignedUrl(
            String objectKey,
            String contentType,
            String contentDisposition
    ) {
        Instant expiresAt = Instant.now().plus(PRESIGNED_URL_DURATION);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(getAttachmentBucket())
                .key(objectKey)
                .responseContentType(contentType)
                .responseContentDisposition(contentDisposition)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(PRESIGNED_URL_DURATION)
                        .getObjectRequest(getObjectRequest)
                        .build();

        try {
            PresignedGetObjectRequest presignedRequest =
                    s3Presigner.presignGetObject(presignRequest);

            return new PresignedUrl(
                    presignedRequest.url().toString(),
                    expiresAt
            );
        } catch (SdkException exception) {
            log.error(
                    "S3 첨부파일 접근 URL 생성에 실패했습니다. objectKey={}",
                    objectKey,
                    exception
            );
            throw new BusinessException(
                    AttachmentErrorCode.ATTACHMENT_URL_GENERATION_FAILED
            );
        }
    }

    /**
     * Content-Disposition 헤더에서 사용할 파일명을 UTF-8로 인코딩한다.
     */
    private String encodeFilename(String originalFilename) {
        return URLEncoder.encode(originalFilename, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }

    /**
     * 문서별 디렉터리 아래에 UUID와 확장자로 구성된 객체 키를 생성한다.
     *
     * @param documentId 문서 ID
     * @param extension 검증된 파일 확장자
     * @return 생성된 S3 객체 키
     */
    private String createObjectKey(Long documentId, String extension) {
        String normalizedExtension = extension.toLowerCase(Locale.ROOT);

        return "%s/%d/%s.%s".formatted(
                ATTACHMENT_KEY_PREFIX,
                documentId,
                UUID.randomUUID(),
                normalizedExtension
        );
    }

    /**
     * 환경에 맞게 설정된 첨부파일 버킷 이름을 반환한다.
     *
     * @return 첨부파일 버킷 이름
     */
    private String getAttachmentBucket() {
        return s3Properties.getS3().getAttachmentBucket();
    }

    /**
     * 생성된 Presigned URL과 만료 시각.
     */
    public record PresignedUrl(String url, Instant expiresAt) {
    }
}
