package com.comin.eapproval.global.security;

import com.comin.eapproval.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 로그아웃 성공 응답을 처리하는 Handler.
 */
@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper;

    /**
     * 로그아웃 성공 시 공통 응답 형식으로 반환한다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authentication 인증 정보
     * @throws IOException 응답 작성 중 발생하는 예외
     */
    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> apiResponse =
                ApiResponse.success("로그아웃에 성공했습니다.");

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}