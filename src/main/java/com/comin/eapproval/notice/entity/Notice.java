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

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean important;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    private LocalDateTime deletedAt;

    @Builder
    private Notice(
            String title,
            String content,
            boolean important,
            Member author
    ) {
        this.title = title;
        this.content = content;
        this.important = important;
        this.author = author;
    }

    /**
     * 공지사항을 생성한다.
     *
     * @param title 제목
     * @param content 내용
     * @param important 중요 공지 여부
     * @param author 작성자
     * @return 생성된 공지사항
     */
    public static Notice create(
            String title,
            String content,
            boolean important,
            Member author
    ) {
        return Notice.builder()
                .title(title.trim())
                .content(content.trim())
                .important(important)
                .author(author)
                .build();
    }

    /**
     * 공지사항 내용을 수정한다.
     *
     * @param title 제목
     * @param content 내용
     * @param important 중요 공지 여부
     */
    public void update(
            String title,
            String content,
            boolean important
    ) {
        this.title = title.trim();
        this.content = content.trim();
        this.important = important;
    }

    /**
     * 공지사항을 삭제 처리한다.
     */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
