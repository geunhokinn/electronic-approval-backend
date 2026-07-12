package com.comin.eapproval.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QueryDSL 사용을 위한 설정 클래스.
 */
@Configuration
@RequiredArgsConstructor
public class QuerydslConfig {

    private final EntityManager entityManager;

    /**
     * QueryDSL 쿼리 생성을 위한 JPAQueryFactory를 등록한다.
     *
     * @return JPAQueryFactory
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
