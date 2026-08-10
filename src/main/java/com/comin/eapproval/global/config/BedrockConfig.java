package com.comin.eapproval.global.config;

import com.comin.eapproval.global.config.properties.BedrockProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Amazon Bedrock 공통 설정을 활성화합니다.
 *
 * 실제 ChatClient와 EmbeddingModel Bean은 Spring AI 자동 설정이 생성하며,
 * 이 클래스는 프로젝트 전용 모델 설정을 함께 검증하고 노출합니다.
 */
@Configuration
@EnableConfigurationProperties(BedrockProperties.class)
public class BedrockConfig {
}
