package com.comin.eapproval.notice.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 공지사항 목록 조회 결과를 제공하는 응답 객체.
 */
@Getter
public class NoticeListResponse {

    private final Long noticeId;
    private final String title;
    private final boolean important;
    private final String authorName;
    private final LocalDateTime createdAt;

    /**
     * 공지사항 목록 조회 응답을 생성한다.
     *
     * @param noticeId 공지사항 ID
     * @param title 제목
     * @param important 중요 공지 여부
     * @param authorName 작성자 이름
     * @param createdAt 작성 시각
     */
    public NoticeListResponse(
            Long noticeId,
            String title,
            boolean important,
            String authorName,
            LocalDateTime createdAt
    ) {
        this.noticeId = noticeId;
        this.title = title;
        this.important = important;
        this.authorName = authorName;
        this.createdAt = createdAt;
    }
}
