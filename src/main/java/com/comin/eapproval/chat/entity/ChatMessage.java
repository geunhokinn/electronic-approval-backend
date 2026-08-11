package com.comin.eapproval.chat.entity;

import com.comin.eapproval.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI 대화방의 메시지를 저장하는 엔티티.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ChatConversation conversation;

    @Column(nullable = false, length = 36)
    private String requestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatMessageRole role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int sequence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatMessageStatus status;

    private String model;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
    private String errorCode;
    private LocalDateTime completedAt;

    private ChatMessage(
            ChatConversation conversation,
            String requestId,
            ChatMessageRole role,
            String content,
            int sequence
    ) {
        this.conversation = conversation;
        this.requestId = requestId;
        this.role = role;
        this.content = content;
        this.sequence = sequence;
        this.status = ChatMessageStatus.RECEIVED;
    }

    public static ChatMessage userMessage(
            ChatConversation conversation,
            String requestId,
            String content,
            int sequence
    ) {
        return new ChatMessage(conversation, requestId, ChatMessageRole.USER, content, sequence);
    }
}
