package com.comin.eapproval.attachment.dto.response;

import com.comin.eapproval.attachment.entity.Attachment;
import lombok.Getter;

/**
 * 첨부파일 업로드 결과를 제공하는 응답 객체.
 */
@Getter
public class AttachmentUploadResponse {

    private final Long attachmentId;
    private final String originalFilename;
    private final String contentType;
    private final long fileSize;

    private AttachmentUploadResponse(
            Long attachmentId,
            String originalFilename,
            String contentType,
            long fileSize
    ) {
        this.attachmentId = attachmentId;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    /**
     * 첨부파일 엔티티로 업로드 응답을 생성한다.
     *
     * @param attachment 첨부파일 엔티티
     * @return 첨부파일 업로드 응답
     */
    public static AttachmentUploadResponse from(Attachment attachment) {
        return new AttachmentUploadResponse(
                attachment.getId(),
                attachment.getOriginalFilename(),
                attachment.getContentType(),
                attachment.getFileSize()
        );
    }
}
