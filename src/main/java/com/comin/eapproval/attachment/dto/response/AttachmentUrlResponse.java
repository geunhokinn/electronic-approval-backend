package com.comin.eapproval.attachment.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 첨부파일에 일시적으로 접근할 수 있는 URL 정보를 제공하는 응답 객체.
 */
@Getter
public class AttachmentUrlResponse {

    private final String url;
    private final LocalDateTime expiresAt;

    private AttachmentUrlResponse(String url, LocalDateTime expiresAt) {
        this.url = url;
        this.expiresAt = expiresAt;
    }

    /**
     * 첨부파일 접근 URL 응답을 생성한다.
     *
     * @param url Presigned URL
     * @param expiresAt URL 만료 시각
     * @return 첨부파일 접근 URL 응답
     */
    public static AttachmentUrlResponse of(
            String url,
            LocalDateTime expiresAt
    ) {
        return new AttachmentUrlResponse(url, expiresAt);
    }
}
