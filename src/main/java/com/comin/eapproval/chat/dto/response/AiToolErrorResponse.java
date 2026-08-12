package com.comin.eapproval.chat.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI Tool 실패 결과를 표현하는 응답 DTO입니다.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AiToolErrorResponse {

    /** Tool 내부에서 사용하는 오류 코드입니다. */
    private final String errorCode;

    /** 사용자에게 노출할 안전한 오류 메시지입니다. */
    private final String message;

    /**
     * 실패 응답을 생성합니다.
     *
     * @param errorCode 내부 오류 코드
     * @param message 안전한 오류 메시지
     * @return Tool 실패 응답
     */
    public static AiToolErrorResponse of(String errorCode, String message) {
        return new AiToolErrorResponse(errorCode, message);
    }
}
