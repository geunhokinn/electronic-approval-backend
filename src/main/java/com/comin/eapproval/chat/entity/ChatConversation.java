package com.comin.eapproval.chat.entity;

import com.comin.eapproval.global.entity.BaseEntity;
import com.comin.eapproval.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자의 AI 대화방 정보를 저장하는 엔티티.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatConversation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 10)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatConversationStatus status;

    @Column(nullable = false)
    private int lastMessageSequence;

    private LocalDateTime lastMessageAt;

    private LocalDateTime deletedAt;

    @Version
    private Long version;

    private ChatConversation(Member member, String title) {
        this.member = member;
        this.title = title;
        this.status = ChatConversationStatus.ACTIVE;
    }

    public static ChatConversation create(Member member, String firstMessage) {
        String title = firstMessage.trim();
        if (title.length() > 10) {
            title = title.substring(0, 10);
        }
        return new ChatConversation(member, title);
    }

    public int nextMessageSequence() {
        return ++lastMessageSequence;
    }

    public void updateLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public void delete() {
        this.status = ChatConversationStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}
