package com.comin.eapproval.global.security;

import com.comin.eapproval.global.exception.security.SecurityErrorCode;
import com.comin.eapproval.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증되지 않은 사용자의 요청에 대한 응답을 처리하는 Handler.
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * 인증되지 않은 요청에 대해 공통 응답 형식으로 반환한다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authenticationException 인증 예외
     * @throws IOException 응답 작성 중 발생하는 예외
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authenticationException
    ) throws IOException {

        SecurityErrorCode errorCode =
                SecurityErrorCode.AUTHENTICATION_REQUIRED;

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> apiResponse =
                ApiResponse.fail(errorCode.getMessage());

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}