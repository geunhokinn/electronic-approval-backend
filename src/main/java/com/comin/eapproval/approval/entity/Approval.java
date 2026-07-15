package com.comin.eapproval.approval.entity;

import com.comin.eapproval.document.entity.Document;
import com.comin.eapproval.global.entity.BaseEntity;
import com.comin.eapproval.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Approval extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private Member approver;

    @Column(length = 500)
    private String comment;

    @Builder
    private Approval(
            Document document,
            Member approver,
            String comment
    ) {
        this.document = document;
        this.approver = approver;
        this.comment = comment;
    }

    /**
     * 최초 결재 요청 정보를 생성한다.
     */
    public static Approval create(
            Document document,
            Member approver
    ) {
        return Approval.builder()
                .document(document)
                .approver(approver)
                .build();
    }

    /**
     * 반려 의견을 저장한다.
     */
    public void reject(String comment) {
        this.comment = comment;
    }

    /**
     * 재상신을 위해 결재 정보를 초기화한다.
     */
    public void resubmit(Member approver) {
        this.approver = approver;
        this.comment = null;
    }
}