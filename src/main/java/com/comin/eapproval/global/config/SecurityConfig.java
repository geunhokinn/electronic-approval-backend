package com.comin.eapproval.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 인증 및 인가 설정을 관리하는 클래스.
 */
@Configuration
public class SecurityConfig {

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder를 등록한다.
     *
     * @return BCrypt 기반 비밀번호 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 애플리케이션의 요청별 접근 권한을 설정한다.
     *
     * 현재는 인증 기능 구현 전이므로 모든 요청을 임시로 허용한다.
     *
     * @param http HTTP 보안 설정 객체
     * @return 보안 필터 체인
     * @throws Exception 보안 설정 중 발생하는 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
