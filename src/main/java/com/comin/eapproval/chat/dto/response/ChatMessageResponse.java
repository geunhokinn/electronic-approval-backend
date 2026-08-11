package com.comin.eapproval.chat.dto.response;

import com.comin.eapproval.chat.entity.ChatMessage;
import com.comin.eapproval.chat.entity.ChatMessageRole;
import com.comin.eapproval.chat.entity.ChatMessageStatus;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * AI 대화 메시지 조회 응답 DTO.
 */
@Getter
public class ChatMessageResponse {
    private final Long messageId;
    private final String requestId;
    private final ChatMessageRole role;
    private final String content;
    private final int sequence;
    private final ChatMessageStatus status;
    private final LocalDateTime createdAt;

    private ChatMessageResponse(ChatMessage message) {
        this.messageId = message.getId();
        this.requestId = message.getRequestId();
        this.role = message.getRole();
        this.content = message.getContent();
        this.sequence = message.getSequence();
        this.status = message.getStatus();
        this.createdAt = message.getCreatedAt();
    }

    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(message);
    }
}
