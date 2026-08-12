package com.comin.eapproval.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** SSE error 이벤트 데이터 DTO. */
@Getter
@AllArgsConstructor
public class SseErrorData {

    /** 내부 오류 식별 코드입니다. */
    private String code;

    /** 사용자에게 표시할 안전한 오류 메시지입니다. */
    private String message;

    /** 동일 요청을 다시 시도할 수 있는지 여부입니다. */
    private boolean retryable;
}
