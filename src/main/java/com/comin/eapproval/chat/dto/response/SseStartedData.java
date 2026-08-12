package com.comin.eapproval.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** SSE started 이벤트 데이터 DTO. */
@Getter
@AllArgsConstructor
public class SseStartedData {

    /** 저장된 USER 메시지 ID입니다. */
    private Long userMessageId;
}
