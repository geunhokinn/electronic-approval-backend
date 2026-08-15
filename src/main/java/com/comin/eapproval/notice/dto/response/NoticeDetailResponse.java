package com.comin.eapproval.notice.dto.response;

import com.comin.eapproval.notice.entity.NoticeCategory;
import com.comin.eapproval.notice.entity.NoticeType;
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
    private final NoticeType noticeType;
    private final NoticeCategory noticeCategory;
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
     * @param noticeType 공지 유형
     * @param noticeCategory 공지 분류
     * @param authorId 작성자 ID
     * @param authorName 작성자 이름
     * @param createdAt 작성 시각
     * @param updatedAt 수정 시각
     */
    public NoticeDetailResponse(
            Long noticeId,
            String title,
            String content,
            NoticeType noticeType,
            NoticeCategory noticeCategory,
            Long authorId,
            String authorName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.noticeType = noticeType;
        this.noticeCategory = noticeCategory;
        this.authorId = authorId;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
