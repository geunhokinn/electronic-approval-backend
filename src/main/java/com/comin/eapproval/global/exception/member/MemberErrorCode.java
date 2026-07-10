package com.comin.eapproval.global.exception.member;

import com.comin.eapproval.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberErrorCode implements ErrorCode {

    DUPLICATE_USERNAME(
            HttpStatus.CONFLICT,
            "이미 사용 중인 아이디입니다."
    ),

    MEMBER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "회원을 찾을 수 없습니다."
    ),

    MEMBER_NOT_ACTIVE(
            HttpStatus.FORBIDDEN,
            "승인되지 않은 회원입니다."
    );

    private final HttpStatus httpStatus;
    private final String message;

    MemberErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
