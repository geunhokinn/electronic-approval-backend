package com.comin.eapproval.regulation.service;

import com.comin.eapproval.global.config.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.util.UUID;

/**
 * 규정 원본 파일을 규정 전용 S3 버킷에 저장합니다.
 */
@Service
@RequiredArgsConstructor
public class RegulationStorageService {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    /**
     * 규정 원본 파일을 S3에 업로드합니다.
     *
     * @param regulationId 규정 ID
     * @param version 규정 버전
     * @param file 업로드 파일
     * @return 저장된 S3 객체 키
     * @throws IOException 파일 스트림을 읽을 수 없는 경우
     */
    public String upload(Long regulationId, String version, MultipartFile file)
            throws IOException {
        String extension = getExtension(file.getOriginalFilename());
        String objectKey = "regulations/%d/%s/%s.%s".formatted(
                regulationId,
                version,
                UUID.randomUUID(),
                extension
        );

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Properties.getS3().getRegulationBucket())
                .key(objectKey)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(
                file.getInputStream(),
                file.getSize()
        ));

        return objectKey;
    }

    /** S3에 업로드된 규정 원본을 삭제합니다. */
    public void delete(String objectKey) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(s3Properties.getS3().getRegulationBucket())
                .key(objectKey)
                .build());
    }

    /**
     * S3에 저장된 규정 원본 파일을 읽습니다.
     *
     * @param objectKey S3 객체 키
     * @return 원본 파일 바이트 데이터
     */
    public byte[] download(String objectKey) {
        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(s3Properties.getS3().getRegulationBucket())
                        .key(objectKey)
                        .build()
        );
        return response.asByteArray();
    }

    /**
     * 파일명에서 S3 저장에 사용할 확장자를 추출합니다.
     *
     * @param filename 원본 파일명
     * @return 소문자로 변환된 확장자
     */
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "bin";
        }

        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
