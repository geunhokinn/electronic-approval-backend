package com.comin.eapproval.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** SSE status 이벤트 데이터 DTO. */
@Getter
@AllArgsConstructor
public class SseStatusData {

    /** 처리 단계 식별 코드입니다. */
    private String code;

    /** 사용자에게 표시할 처리 상태 문구입니다. */
    private String message;
}
