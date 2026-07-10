package com.comin.eapproval.document.entity;

import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.comin.eapproval.global.entity.BaseEntity;
import com.comin.eapproval.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DocumentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private Member approver;

    // @Version
    private Long version;

    private LocalDateTime deletedAt;

    @Builder
    private Document(
            String title,
            String content,
            DocumentStatus status,
            Member writer,
            Member approver
    ) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.writer = writer;
        this.approver = approver;
    }

    public static Document createDraft(
            String title,
            String content,
            Member writer
    ) {
        return Document.builder()
                .title(title)
                .content(content)
                .status(DocumentStatus.DRAFT)
                .writer(writer)
                .build();
    }

    public void assignApprover(Member approver) {
        this.approver = approver;
    }

    public void requestApproval() {
        this.status = DocumentStatus.PENDING;
    }

    public void approve() {
        this.status = DocumentStatus.APPROVED;
    }

    public void reject() {
        this.status = DocumentStatus.REJECTED;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
