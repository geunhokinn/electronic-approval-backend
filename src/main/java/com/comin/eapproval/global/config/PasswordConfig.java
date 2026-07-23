package com.comin.eapproval.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 암호화 관련 빈을 관리하는 설정 클래스.
 */
@Configuration
public class PasswordConfig {

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder를 등록한다.
     *
     * @return BCrypt 기반 비밀번호 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}