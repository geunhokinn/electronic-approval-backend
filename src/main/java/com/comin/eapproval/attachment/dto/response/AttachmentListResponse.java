package com.comin.eapproval.attachment.dto.response;

import com.comin.eapproval.attachment.entity.Attachment;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 첨부파일 목록 조회 결과를 제공하는 응답 객체.
 */
@Getter
public class AttachmentListResponse {

    private final Long attachmentId;
    private final String originalFilename;
    private final String contentType;
    private final long fileSize;
    private final LocalDateTime createdAt;

    private AttachmentListResponse(
            Long attachmentId,
            String originalFilename,
            String contentType,
            long fileSize,
            LocalDateTime createdAt
    ) {
        this.attachmentId = attachmentId;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.createdAt = createdAt;
    }

    /**
     * 첨부파일 엔티티로 목록 응답을 생성한다.
     *
     * @param attachment 첨부파일 엔티티
     * @return 첨부파일 목록 응답
     */
    public static AttachmentListResponse from(Attachment attachment) {
        return new AttachmentListResponse(
                attachment.getId(),
                attachment.getOriginalFilename(),
                attachment.getContentType(),
                attachment.getFileSize(),
                attachment.getCreatedAt()
        );
    }
}
