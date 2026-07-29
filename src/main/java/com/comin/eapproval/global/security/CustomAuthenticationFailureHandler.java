package com.comin.eapproval.global.security;

import com.comin.eapproval.global.exception.security.AccountInactiveException;
import com.comin.eapproval.global.exception.security.AccountPendingException;
import com.comin.eapproval.global.exception.security.AccountRejectedException;
import com.comin.eapproval.global.exception.security.SecurityErrorCode;
import com.comin.eapproval.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 로그인 실패 응답을 처리하는 Handler.
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler
        implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    /**
     * 로그인 실패 시 공통 응답 형식으로 반환한다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authenticationException 인증 실패 예외
     * @throws IOException 응답 작성 중 발생하는 예외
     */
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authenticationException
    ) throws IOException {

        SecurityErrorCode errorCode;

        if (hasCause(authenticationException, AccountPendingException.class)) {
            errorCode = SecurityErrorCode.ACCOUNT_PENDING;
        } else if (hasCause(authenticationException, AccountInactiveException.class)) {
            errorCode = SecurityErrorCode.ACCOUNT_INACTIVE;
        } else if (hasCause(authenticationException, AccountRejectedException.class)) {
            errorCode = SecurityErrorCode.ACCOUNT_REJECTED;
        } else {
            errorCode = SecurityErrorCode.LOGIN_FAILED;
        }

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> apiResponse =
                ApiResponse.fail(errorCode.getMessage());

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }

    /**
     * Spring Security가 감싼 인증 예외의 원인까지 확인한다.
     *
     * @param throwable 확인할 예외
     * @param exceptionType 찾을 예외 타입
     * @return 원인 예외에 해당 타입이 포함되어 있으면 true
     */
    private boolean hasCause(
            Throwable throwable,
            Class<? extends AuthenticationException> exceptionType
    ) {
        Throwable cause = throwable;

        while (cause != null) {
            if (exceptionType.isInstance(cause)) {
                return true;
            }

            cause = cause.getCause();
        }

        return false;
    }
}
