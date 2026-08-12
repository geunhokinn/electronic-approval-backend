package com.comin.eapproval.chat.service;

import com.comin.eapproval.chat.dto.response.RagEvidence;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 규정 근거가 포함된 프롬프트를 Nova 2 Lite에 전달하여 답변을 생성하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class RagAnswerService {

    private static final String MODEL = "amazon.nova-2-lite-v1:0";

    private final ChatClient.Builder chatClientBuilder;
    private final RagPromptService promptService;
    private final RagEvidenceValidationService evidenceValidationService;

    /**
     * 규정 근거를 기반으로 AI 답변을 생성한다.
     *
     * @param question 사용자 질문
     * @param evidences Nova에 전달할 규정 근거
     * @return Nova가 생성한 규정 답변
     */
    public String generate(String question, List<RagEvidence> evidences) {
        if (!evidenceValidationService.hasUsableEvidence(evidences)) {
            return "관련 규정을 찾지 못했습니다.";
        }

        return chatClientBuilder
                .build()
                .prompt()
                .user(promptService.build(question, evidences))
                .call()
                .content();
    }

    /**
     * 현재 사용하는 Bedrock 모델 ID를 반환한다.
     *
     * @return Nova 2 Lite 모델 ID
     */
    public String getModel() {
        return MODEL;
    }
}
