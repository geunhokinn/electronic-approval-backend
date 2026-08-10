package com.comin.eapproval.global.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Amazon Bedrock 채팅 및 임베딩 모델 설정을 관리합니다.
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "ai.bedrock")
public class BedrockProperties {

    @NotBlank
    private String region;

    @NotBlank
    private String chatModel;

    @NotBlank
    private String embeddingModel;
}
