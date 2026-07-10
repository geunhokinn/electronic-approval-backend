package com.comin.eapproval.approvalHistory.entity;

import com.comin.eapproval.approvalHistory.entity.enums.ApprovalHistoryAction;
import com.comin.eapproval.document.entity.Document;
import com.comin.eapproval.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApprovalHistoryAction action;

    @Column(length = 500)
    private String comment;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private ApprovalHistory(
            Document document,
            Member member,
            ApprovalHistoryAction action,
            String comment
    ) {
        this.document = document;
        this.member = member;
        this.action = action;
        this.comment = comment;
    }

    public static ApprovalHistory pending(Document document, Member member) {
        return ApprovalHistory.builder()
                .document(document)
                .member(member)
                .action(ApprovalHistoryAction.PENDING)
                .build();
    }

    public static ApprovalHistory approved(Document document, Member member) {
        return ApprovalHistory.builder()
                .document(document)
                .member(member)
                .action(ApprovalHistoryAction.APPROVED)
                .build();
    }

    public static ApprovalHistory rejected(Document document, Member member, String comment) {
        return ApprovalHistory.builder()
                .document(document)
                .member(member)
                .action(ApprovalHistoryAction.REJECTED)
                .comment(comment)
                .build();
    }
}
