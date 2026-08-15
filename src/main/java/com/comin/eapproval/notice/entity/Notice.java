package com.comin.eapproval.notice.entity;

import com.comin.eapproval.global.entity.BaseEntity;
import com.comin.eapproval.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공지사항 정보를 저장하는 엔티티.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NoticeType noticeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NoticeCategory noticeCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    private LocalDateTime deletedAt;

    @Builder
    private Notice(
            String title,
            String content,
            NoticeType noticeType,
            NoticeCategory noticeCategory,
            Member author
    ) {
        this.title = title;
        this.content = content;
        this.noticeType = noticeType;
        this.noticeCategory = noticeCategory;
        this.author = author;
    }

    /**
     * 공지사항을 생성한다.
     *
     * @param title 제목
     * @param content 내용
     * @param noticeType 공지 유형
     * @param noticeCategory 공지 분류
     * @param author 작성자
     * @return 생성된 공지사항
     */
    public static Notice create(
            String title,
            String content,
            NoticeType noticeType,
            NoticeCategory noticeCategory,
            Member author
    ) {
        return Notice.builder()
                .title(title.trim())
                .content(content.trim())
                .noticeType(noticeType)
                .noticeCategory(noticeCategory)
                .author(author)
                .build();
    }

    /**
     * 공지사항 내용을 수정한다.
     *
     * @param title 제목
     * @param content 내용
     * @param noticeType 공지 유형
     * @param noticeCategory 공지 분류
     */
    public void update(
            String title,
            String content,
            NoticeType noticeType,
            NoticeCategory noticeCategory
    ) {
        this.title = title.trim();
        this.content = content.trim();
        this.noticeType = noticeType;
        this.noticeCategory = noticeCategory;
    }

    /**
     * 공지사항을 삭제 처리한다.
     */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
