package com.comin.eapproval.global.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * AWS S3 첨부파일 저장소 설정값을 관리하는 프로퍼티 클래스입니다.
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "aws")
public class S3Properties {

    @NotBlank
    private String region;

    @Valid
    private S3 s3 = new S3();

    /**
     * S3 첨부파일 버킷 설정값을 관리합니다.
     */
    @Getter
    @Setter
    public static class S3 {

        @NotBlank
        private String attachmentBucket;
    }
}
