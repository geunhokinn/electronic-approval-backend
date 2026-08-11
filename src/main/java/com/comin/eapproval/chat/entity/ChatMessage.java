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

    /**
     * AI 응답 메시지를 생성한다.
     *
     * @param conversation 대화방
     * @param requestId 요청 식별자
     * @param sequence 메시지 순번
     * @return AI 응답 메시지
     */
    public static ChatMessage assistantMessage(
            ChatConversation conversation,
            String requestId,
            int sequence
    ) {
        return new ChatMessage(
                conversation,
                requestId,
                ChatMessageRole.ASSISTANT,
                "",
                sequence
        );
    }

    /** AI 응답 처리를 시작한다. */
    public void startProcessing() {
        this.status = ChatMessageStatus.PROCESSING;
    }

    /**
     * AI 응답을 완료하고 전체 응답과 사용량을 저장한다.
     */
    public void complete(
            String content,
            String model,
            Integer promptTokens,
            Integer completionTokens,
            Integer totalTokens
    ) {
        this.content = content;
        this.model = model;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.totalTokens = totalTokens;
        this.status = ChatMessageStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    /** AI 응답 처리를 실패 상태로 변경한다. */
    public void fail(String errorCode) {
        this.status = ChatMessageStatus.FAILED;
        this.errorCode = errorCode;
        this.completedAt = LocalDateTime.now();
    }

    /** 사용자가 중지한 AI 응답을 취소 상태로 변경한다. */
    public void cancel() {
        this.status = ChatMessageStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
    }
}
