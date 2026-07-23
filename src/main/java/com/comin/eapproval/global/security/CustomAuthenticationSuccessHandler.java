package com.comin.eapproval.global.security;

import com.comin.eapproval.global.response.ApiResponse;
import com.comin.eapproval.member.dto.response.CurrentMemberResponse;
import com.comin.eapproval.member.security.CustomUserDetails;
import com.comin.eapproval.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 로그인 성공 응답을 처리하는 Handler.
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    private final MemberService memberService;
    private final ObjectMapper objectMapper;

    /**
     * 로그인 성공 시 현재 로그인한 회원 정보를
     * 공통 응답 형식으로 반환한다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authentication 인증 정보
     * @throws IOException 응답 작성 중 발생하는 예외
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        CurrentMemberResponse currentMemberResponse =
                memberService.getCurrentMember(
                        userDetails.getMemberId()
                );

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<CurrentMemberResponse> apiResponse =
                ApiResponse.success(currentMemberResponse);

        objectMapper.writeValue(
                response.getWriter(),
                apiResponse
        );
    }
}