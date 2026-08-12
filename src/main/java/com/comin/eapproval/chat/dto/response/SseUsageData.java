package com.comin.eapproval.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Nova 응답 토큰 사용량 DTO. */
@Getter @AllArgsConstructor
public class SseUsageData {

    /** AI 입력에 사용된 토큰 수입니다. */
    private Integer promptTokens;

    /** AI가 생성한 출력 토큰 수입니다. */
    private Integer completionTokens;

    /** 입력 토큰과 출력 토큰의 합계입니다. */
    private Integer totalTokens;
}
