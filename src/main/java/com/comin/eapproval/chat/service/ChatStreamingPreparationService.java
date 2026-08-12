package com.comin.eapproval.chat.service;

import com.comin.eapproval.chat.dto.request.ChatStreamingRequest;
import com.comin.eapproval.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SSE 요청을 검증하고 AI 처리 전에 USER 메시지를 저장하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class ChatStreamingPreparationService {

    private final ChatConversationService conversationService;

    /**
     * 첫 메시지 또는 기존 대화방 메시지를 저장한다.
     *
     * @param memberId 로그인 사용자 ID
     * @param request 스트리밍 요청
     * @return 저장된 USER 메시지
     */
    @Transactional
    public ChatMessage saveUserMessage(
            Long memberId,
            ChatStreamingRequest request
    ) {
        var conversation = conversationService.saveUserMessage(
                memberId,
                new com.comin.eapproval.chat.dto.request.ChatMessageCreateRequest(
                        request.getConversationId(),
                        request.getRequestId(),
                        request.getMessage()
                )
        );

        return conversationService.findUserMessage(
                memberId,
                conversation.getId(),
                request.getRequestId()
        );
    }
}
