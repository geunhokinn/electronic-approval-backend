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
    /** 대화방 식별자입니다. */
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    /** 대화방을 생성한 회원입니다. */
    private Member member;

    @Column(nullable = false, length = 10)
    /** 첫 메시지를 기반으로 생성한 대화방 제목입니다. */
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    /** 대화방 상태입니다. */
    private ChatConversationStatus status;

    /** 마지막 메시지가 저장된 시각입니다. */
    private LocalDateTime lastMessageAt;

    /** 대화방이 논리 삭제된 시각입니다. */
    private LocalDateTime deletedAt;

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

    public void updateLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public void delete() {
        this.status = ChatConversationStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}
