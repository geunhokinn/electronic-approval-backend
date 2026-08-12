package com.comin.eapproval.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** SSE delta 이벤트 데이터 DTO. */
@Getter
@AllArgsConstructor
public class SseDeltaData {

    /** AI가 생성한 답변 조각입니다. */
    private String content;
}
