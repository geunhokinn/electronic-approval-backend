package com.comin.eapproval.chat.dto.response;

import com.comin.eapproval.chat.entity.ChatConversation;
import com.comin.eapproval.chat.entity.ChatConversationStatus;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 대화방 조회 응답 DTO.
 */
@Getter
public class ChatConversationResponse {
    private final Long conversationId;
    private final String title;
    private final ChatConversationStatus status;
    private final LocalDateTime lastMessageAt;
    private final LocalDateTime createdAt;
    private final List<ChatMessageResponse> messages;

    private ChatConversationResponse(ChatConversation conversation, List<ChatMessageResponse> messages) {
        this.conversationId = conversation.getId();
        this.title = conversation.getTitle();
        this.status = conversation.getStatus();
        this.lastMessageAt = conversation.getLastMessageAt();
        this.createdAt = conversation.getCreatedAt();
        this.messages = messages;
    }

    public static ChatConversationResponse from(ChatConversation conversation, List<ChatMessageResponse> messages) {
        return new ChatConversationResponse(conversation, messages);
    }
}
