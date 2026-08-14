package com.comin.eapproval.dashboard.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 대시보드에 표시할 공지사항 정보를 제공하는 응답 객체.
 */
@Getter
public class DashboardNoticeResponse {

    /**
     * 공지사항 ID.
     */
    private final Long noticeId;

    /**
     * 공지사항 제목.
     */
    private final String title;

    /**
     * 중요 공지 여부.
     */
    private final boolean isImportant;

    /**
     * 공지사항 작성일.
     */
    private final LocalDateTime createdAt;

    /**
     * 대시보드 공지사항 응답을 생성한다.
     *
     * @param noticeId 공지사항 ID
     * @param title 공지사항 제목
     * @param isImportant 중요 공지 여부
     * @param createdAt 공지사항 작성일
     */
    @QueryProjection
    public DashboardNoticeResponse(
            Long noticeId,
            String title,
            boolean isImportant,
            LocalDateTime createdAt
    ) {
        this.noticeId = noticeId;
        this.title = title;
        this.isImportant = isImportant;
        this.createdAt = createdAt;
    }
}
