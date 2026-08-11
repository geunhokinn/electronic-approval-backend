package com.comin.eapproval.chat.service;

import com.comin.eapproval.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AI 응답 생성 결과를 ASSISTANT 메시지로 저장하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class ChatResponseService {

    private final ChatConversationService conversationService;
    private final ChatGenerationService generationService;

    /**
     * 최근 대화 맥락으로 AI 응답을 생성하고 완료된 메시지로 저장한다.
     *
     * @param memberId 사용자 ID
     * @param conversationId 대화방 ID
     * @param requestId 요청 식별자
     * @return 저장된 ASSISTANT 메시지
     */
    @Transactional
    public ChatMessage generateAndSave(
            Long memberId,
            Long conversationId,
            String requestId
    ) {
        ChatMessage assistantMessage = conversationService.startAssistantMessage(
                memberId,
                conversationId,
                requestId
        );

        String content = generationService.generate(
                conversationService.getRecentMessagesForAi(
                        memberId,
                        conversationId
                )
        );

        conversationService.completeAssistantMessage(
                assistantMessage.getId(),
                content,
                "amazon.nova-2-lite-v1:0",
                null,
                null,
                null
        );

        return assistantMessage;
    }
}
