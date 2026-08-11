package com.comin.eapproval.global.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Qdrant 연결과 규정 컬렉션 설정을 관리합니다. */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "qdrant")
public class QdrantProperties {

    /** Qdrant 호스트입니다. */
    @NotBlank
    private String host;

    /** Qdrant REST API 포트입니다. */
    @Min(1)
    private int port;

    /** 규정 벡터를 저장할 컬렉션입니다. */
    @NotBlank
    private String collection;
}
