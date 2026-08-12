package com.comin.eapproval.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.OffsetDateTime;

/** 모든 SSE 이벤트가 공유하는 공통 이벤트 DTO. */
@Getter
@AllArgsConstructor
public class SseEvent<T> {

    /** 이벤트 종류입니다. */
    private SseEventType type;

    /** 이벤트가 발생한 대화방 ID입니다. */
    private Long conversationId;

    /** 클라이언트 요청 식별자입니다. */
    private String requestId;

    /** 이벤트 발생 시각입니다. */
    private OffsetDateTime timestamp;

    /** 이벤트 종류별 상세 데이터입니다. */
    private T data;

    /** 공통 SSE 이벤트를 생성한다. */
    public static <T> SseEvent<T> of(SseEventType type, Long conversationId, String requestId, T data) {
        return new SseEvent<>(type, conversationId, requestId, OffsetDateTime.now(), data);
    }
}
