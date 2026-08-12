package com.comin.eapproval.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

/** SSE completed 이벤트 데이터 DTO. */
@Getter
@AllArgsConstructor
public class SseCompletedData {

    /** 저장된 ASSISTANT 메시지 ID입니다. */
    private Long assistantMessageId;

    /** AI가 생성한 전체 답변입니다. */
    private String content;

    /** 답변에 사용된 규정 출처 목록입니다. */
    private List<RagSource> sources;

    /** AI 응답에 사용된 토큰 사용량입니다. */
    private SseUsageData usage;
}
