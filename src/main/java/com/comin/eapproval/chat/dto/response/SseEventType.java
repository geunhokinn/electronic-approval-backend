package com.comin.eapproval.chat.dto.response;

/** AI 채팅 SSE 이벤트 종류를 정의한다. */
public enum SseEventType {

    /** 요청 접수와 USER 메시지 저장 완료 이벤트입니다. */
    STARTED,

    /** 서버의 처리 단계 안내 이벤트입니다. */
    STATUS,

    /** AI 답변 일부가 생성된 이벤트입니다. */
    DELTA,

    /** AI 답변 생성과 저장이 완료된 이벤트입니다. */
    COMPLETED,

    /** 처리 중 오류가 발생한 이벤트입니다. */
    ERROR
}
