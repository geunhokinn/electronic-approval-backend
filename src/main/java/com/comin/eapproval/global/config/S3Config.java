package com.comin.eapproval.global.config;

import com.comin.eapproval.global.config.properties.S3Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * AWS S3 클라이언트 설정을 관리하는 클래스입니다.
 */
@Configuration
@EnableConfigurationProperties(S3Properties.class)
public class S3Config {

    /**
     * 실행 환경에 맞는 AWS 자격 증명을 탐색하는 Provider를 등록합니다.
     *
     * @return AWS 기본 자격 증명 Provider
     */
    @Bean
    public DefaultCredentialsProvider awsCredentialsProvider() {
        return DefaultCredentialsProvider.builder().build();
    }

    /**
     * S3 객체 작업에 사용하는 동기식 클라이언트를 등록합니다.
     *
     * @param properties S3 환경설정
     * @param credentialsProvider AWS 자격 증명 Provider
     * @return S3 동기식 클라이언트
     */
    @Bean
    public S3Client s3Client(
            S3Properties properties,
            DefaultCredentialsProvider credentialsProvider
    ) {
        return S3Client.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    /**
     * 첨부파일 미리보기와 다운로드 URL 생성에 사용하는 Presigner를 등록합니다.
     *
     * @param properties S3 환경설정
     * @param credentialsProvider AWS 자격 증명 Provider
     * @return S3 Presigner
     */
    @Bean
    public S3Presigner s3Presigner(
            S3Properties properties,
            DefaultCredentialsProvider credentialsProvider
    ) {
        return S3Presigner.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
