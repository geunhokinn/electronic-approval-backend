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
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
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

    private final S3Client s3Client;
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
}
