package com.comin.eapproval.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * AI 규정 답변과 답변에 사용된 출처를 함께 반환하는 응답 DTO.
 */
@Getter
@AllArgsConstructor
public class RagAnswerResponse {

    private String answer;
    private List<RagSource> sources;

    /**
     * 답변과 출처 목록으로 응답을 생성한다.
     *
     * @param answer AI가 생성한 답변
     * @param sources 답변에 사용된 규정 출처
     * @return RAG 답변 응답
     */
    public static RagAnswerResponse of(
            String answer,
            List<RagSource> sources
    ) {
        return new RagAnswerResponse(
                answer,
                sources == null ? List.of() : List.copyOf(sources)
        );
    }
}
