package com.comin.eapproval.regulation.service;

import com.comin.eapproval.global.config.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "bin";
        }

        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
