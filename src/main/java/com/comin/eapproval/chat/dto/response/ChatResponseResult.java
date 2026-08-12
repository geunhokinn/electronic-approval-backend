package com.comin.eapproval.chat.dto.response;

import com.comin.eapproval.chat.entity.ChatMessage;
import lombok.Getter;

/**
 * AI 응답 생성 결과와 SSE 전송에 필요한 식별자를 담는 DTO.
 */
@Getter
public class ChatResponseResult {

    private final Long conversationId;
    private final String requestId;
    private final Long userMessageId;
    private final Long assistantMessageId;
    private final String status;
    private final String content;
    private final String errorCode;

    private ChatResponseResult(
            Long conversationId,
            String requestId,
            Long userMessageId,
            Long assistantMessageId,
            String status,
            String content,
            String errorCode
    ) {
        this.conversationId = conversationId;
        this.requestId = requestId;
        this.userMessageId = userMessageId;
        this.assistantMessageId = assistantMessageId;
        this.status = status;
        this.content = content;
        this.errorCode = errorCode;
    }

    /**
     * 완료된 AI 응답 결과를 생성한다.
     *
     * @param userMessage 사용자 메시지
     * @param assistantMessage AI 메시지
     * @return 응답 결과
     */
    public static ChatResponseResult completed(
            ChatMessage userMessage,
            ChatMessage assistantMessage
    ) {
        return new ChatResponseResult(
                assistantMessage.getConversation().getId(),
                assistantMessage.getRequestId(),
                userMessage.getId(),
                assistantMessage.getId(),
                assistantMessage.getStatus().name(),
                assistantMessage.getContent(),
                assistantMessage.getErrorCode()
        );
    }
}
