package com.comin.eapproval.global.exception.security;

import com.comin.eapproval.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Spring Security에서 발생하는 오류 코드를 정의한다.
 */
@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ErrorCode {

    /**
     * 로그인에 성공했지만 아직 관리자 승인이 완료되지 않은 계정.
     */
    ACCOUNT_PENDING(
            HttpStatus.FORBIDDEN,
            "가입 승인 대기 중입니다. 관리자 승인 후 이용 가능합니다."
    ),

    /**
     * 관리자가 비활성화한 계정.
     */
    ACCOUNT_INACTIVE(
            HttpStatus.FORBIDDEN,
            "비활성화된 계정입니다."
    ),

    /**
     * 관리자가 가입을 거부한 계정.
     */
    ACCOUNT_REJECTED(
            HttpStatus.FORBIDDEN,
            "가입이 거절된 계정입니다. 관리자에게 문의하세요."
    ),

    /**
     * 아이디 또는 비밀번호가 올바르지 않은 경우.
     */
    LOGIN_FAILED(
            HttpStatus.UNAUTHORIZED,
            "아이디 또는 비밀번호가 올바르지 않습니다."
    ),

    /**
     * 인증되지 않은 사용자의 요청.
     */
    AUTHENTICATION_REQUIRED(
            HttpStatus.UNAUTHORIZED,
            "로그인이 필요합니다."
    ),

    /**
     * 권한이 없는 사용자의 요청.
     */
    ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "접근 권한이 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String message;
}
