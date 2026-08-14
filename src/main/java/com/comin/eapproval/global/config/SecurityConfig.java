package com.comin.eapproval.global.config;

import com.comin.eapproval.global.security.*;
import com.comin.eapproval.member.security.CustomUserDetailsChecker;
import com.comin.eapproval.member.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 인증 및 인가 설정을 관리하는 클래스.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomUserDetailsChecker customUserDetailsChecker;
    private final PasswordEncoder passwordEncoder;

//    /**
//     * 비밀번호 암호화를 위한 PasswordEncoder를 등록한다.
//     *
//     * @return BCrypt 기반 비밀번호 인코더
//     */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    /**
     * 애플리케이션의 인증 및 로그아웃 정책을 설정한다.
     *
     * @param http HTTP 보안 설정 객체
     * @return 보안 필터 체인
     * @throws Exception 보안 설정 중 발생하는 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .authenticationProvider(authenticationProvider())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/api/login",
                                "/api/logout",
                                "/api/members/signup",
                                "/api/admin/**",
                                "/api/documents/**",
                                "/api/approvals/**",
                                "/api/chat/messages/stream",
                                "/api/chat/conversations/**"
                        )
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/login",
                                "/api/members/signup",
                                "/api/members/check-username"
                        ).permitAll()

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/api/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    /**
     * 회원 조회, 비밀번호 검증, 계정 상태 검사를 수행하는 인증 제공자를 등록한다.
     *
     * @return 사용자 인증 제공자
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(customUserDetailsService);

        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setPreAuthenticationChecks(
                customUserDetailsChecker
        );

        return authenticationProvider;
    }
}
