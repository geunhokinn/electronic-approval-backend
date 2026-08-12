package com.comin.eapproval.chat.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI Tool 실행 결과를 일관된 형식으로 전달하는 공통 응답 DTO입니다.
 *
 * @param <T> Tool 실행 결과 데이터 타입
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AiToolResponse<T> {

    /** Tool 실행 성공 여부입니다. */
    private final boolean success;

    /** Tool 실행 결과 데이터입니다. */
    private final T data;

    /** 실패 시 사용할 내부 오류 코드입니다. */
    private final String errorCode;

    /** 사용자에게 전달할 안전한 메시지입니다. */
    private final String message;

    /**
     * 성공 응답을 생성합니다.
     *
     * @param data Tool 실행 결과
     * @param <T> 결과 데이터 타입
     * @return 성공 응답
     */
    public static <T> AiToolResponse<T> success(T data) {
        return new AiToolResponse<>(true, data, null, null);
    }

    /**
     * 실패 응답을 생성합니다.
     *
     * @param errorCode 내부 오류 코드
     * @param message 안전한 오류 메시지
     * @param <T> 결과 데이터 타입
     * @return 실패 응답
     */
    public static <T> AiToolResponse<T> failure(String errorCode, String message) {
        return new AiToolResponse<>(false, null, errorCode, message);
    }
}
