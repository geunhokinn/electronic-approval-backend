package com.comin.eapproval.global.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Amazon Bedrock 모델 연결에 필요한 공통 설정을 관리합니다.
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "ai.bedrock")
public class BedrockProperties {

    /** Bedrock API를 호출할 AWS 리전입니다. */
    @NotBlank
    private String region;

    /** 대화형 응답에 사용할 채팅 모델 ID입니다. */
    @NotBlank
    private String chatModel;

    /** 문서와 질문을 벡터로 변환할 임베딩 모델 ID입니다. */
    @NotBlank
    private String embeddingModel;
}
