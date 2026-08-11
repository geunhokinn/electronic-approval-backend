package com.comin.eapproval.global.exception.chat;

import com.comin.eapproval.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 채팅 기능에서 발생하는 예외 코드를 정의한다.
 */
@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    CHAT_CONVERSATION_LIMIT_EXCEEDED(
            HttpStatus.CONFLICT,
            "채팅방은 사용자당 최대 10개까지 생성할 수 있습니다."),

    CHAT_CONVERSATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "채팅방을 찾을 수 없습니다."),

    CHAT_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "해당 채팅방에 접근할 권한이 없습니다."),

    CHAT_MEMBER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "회원을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
