package com.comin.eapproval.global.config;

import com.comin.eapproval.global.config.properties.QdrantProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Qdrant 연결 설정을 애플리케이션에 등록합니다. */
@Configuration
@EnableConfigurationProperties(QdrantProperties.class)
public class QdrantConfig {
}
