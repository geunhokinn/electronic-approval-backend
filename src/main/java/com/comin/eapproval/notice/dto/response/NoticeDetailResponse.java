package com.comin.eapproval.notice.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 공지사항 상세 조회 결과를 제공하는 응답 객체.
 */
@Getter
public class NoticeDetailResponse {

    private final Long noticeId;
    private final String title;
    private final String content;
    private final boolean important;
    private final Long authorId;
    private final String authorName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /**
     * 공지사항 상세 조회 응답을 생성한다.
     *
     * @param noticeId 공지사항 ID
     * @param title 제목
     * @param content 내용
     * @param important 중요 공지 여부
     * @param authorId 작성자 ID
     * @param authorName 작성자 이름
     * @param createdAt 작성 시각
     * @param updatedAt 수정 시각
     */
    public NoticeDetailResponse(
            Long noticeId,
            String title,
            String content,
            boolean important,
            Long authorId,
            String authorName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.important = important;
        this.authorId = authorId;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
