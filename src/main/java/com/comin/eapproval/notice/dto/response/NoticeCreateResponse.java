package com.comin.eapproval.notice.dto.response;

import lombok.Getter;

/**
 * 공지사항 등록 결과를 제공하는 응답 객체.
 */
@Getter
public class NoticeCreateResponse {

    private final Long noticeId;

    private NoticeCreateResponse(Long noticeId) {
        this.noticeId = noticeId;
    }

    /**
     * 공지사항 ID로 등록 응답을 생성한다.
     *
     * @param noticeId 공지사항 ID
     * @return 공지사항 등록 응답
     */
    public static NoticeCreateResponse of(Long noticeId) {
        return new NoticeCreateResponse(noticeId);
    }
}
