package com.comin.eapproval.global.config;

import com.comin.eapproval.global.config.properties.BedrockProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

/**
 * Amazon Bedrock 공통 설정 프로퍼티를 애플리케이션에 등록합니다.
 *
 * 실제 ChatClient와 EmbeddingModel Bean은 Spring AI 자동 설정을 사용하며,
 * 이 설정 클래스는 프로젝트 전용 모델 설정을 타입 안전하게 관리합니다.
 */
@Configuration
@EnableConfigurationProperties(BedrockProperties.class)
public class BedrockConfig {

    /** Bedrock 임베딩 API 호출에 사용할 AWS 클라이언트를 생성합니다. */
    @Bean
    public BedrockRuntimeClient bedrockRuntimeClient(BedrockProperties properties) {
        return BedrockRuntimeClient.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
