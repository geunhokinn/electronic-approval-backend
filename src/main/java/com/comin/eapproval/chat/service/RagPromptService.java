package com.comin.eapproval.chat.service;

import com.comin.eapproval.chat.dto.response.RagEvidence;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 규정 검색 결과와 사용자 질문을 Nova 전달용 프롬프트로 구성하는 서비스.
 */
@Service
public class RagPromptService {

    private static final String SYSTEM_INSTRUCTION = """
            당신은 금융권 공통 업무를 지원하는 전자결재 AI입니다.
            아래에 제공된 규정 근거만 사용하여 사용자의 질문에 답변하세요.
            근거에 없는 규정 내용이나 수치를 추측하지 마세요.
            근거가 부족하거나 관련 규정을 찾지 못한 경우에는
            '관련 규정을 찾지 못했습니다.'라고 안내하세요.
            답변에는 실제로 사용한 규정의 출처만 표시하세요.
            출처는 규정명, 조항, 버전, 시행일을 포함해야 합니다.
            """;

    /**
     * 규정 근거와 사용자 질문을 하나의 프롬프트로 결합한다.
     *
     * @param question 사용자 질문
     * @param evidences Nova에 전달할 규정 근거
     * @return Nova 전달용 프롬프트
     */
    public String build(String question, List<RagEvidence> evidences) {
        StringBuilder prompt = new StringBuilder(SYSTEM_INSTRUCTION)
                .append("\n[규정 근거]\n");

        if (evidences == null || evidences.isEmpty()) {
            prompt.append("관련 규정 근거가 없습니다.\n");
        } else {
            for (int index = 0; index < evidences.size(); index++) {
                appendEvidence(prompt, index + 1, evidences.get(index));
            }
        }

        return prompt
                .append("\n[사용자 질문]\n")
                .append(question == null ? "" : question.trim())
                .toString();
    }

    private void appendEvidence(
            StringBuilder prompt,
            int index,
            RagEvidence evidence
    ) {
        prompt.append("[근거 ").append(index).append("]\n")
                .append("규정명: ").append(valueOrEmpty(evidence.getRegulationName())).append("\n")
                .append("조항: ").append(valueOrEmpty(evidence.getArticleNumber())).append("\n")
                .append("조항 제목: ").append(valueOrEmpty(evidence.getArticleTitle())).append("\n")
                .append("항: ").append(valueOrEmpty(evidence.getParagraphNumber())).append("\n")
                .append("버전: ").append(valueOrEmpty(evidence.getVersion())).append("\n")
                .append("시행일: ").append(valueOrEmpty(evidence.getEffectiveDate())).append("\n")
                .append("본문:\n").append(valueOrEmpty(evidence.getContent())).append("\n\n");
    }

    private String valueOrEmpty(Object value) {
        return value == null ? "" : value.toString();
    }
}
