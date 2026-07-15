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

    // @Version
    private Long version;

    private LocalDateTime deletedAt;

    @Builder
    private Document(
            String title,
            String content,
            DocumentStatus status,
            Member writer
    ) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.writer = writer;
    }

    /**
     * 임시 저장 문서를 생성한다.
     */
    public static Document create(
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

    /**
     * 문서 내용을 수정한다.
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * 문서를 삭제 처리한다.
     */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 결재 요청한다.
     */
    public void requestApproval() {
        this.status = DocumentStatus.PENDING;
    }

    /**
     * 승인 처리한다.
     */
    public void approve() {
        this.status = DocumentStatus.APPROVED;
    }

    /**
     * 반려 처리한다.
     */
    public void reject() {
        this.status = DocumentStatus.REJECTED;
    }

}
