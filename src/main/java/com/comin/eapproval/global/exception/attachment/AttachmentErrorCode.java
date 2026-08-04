package com.comin.eapproval.global.exception.attachment;

import com.comin.eapproval.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 첨부파일 도메인에서 발생하는 오류 코드를 정의한다.
 */
@Getter
@RequiredArgsConstructor
public enum AttachmentErrorCode implements ErrorCode {

    ATTACHMENT_UPLOAD_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "첨부파일 업로드에 실패했습니다."
    ),
    ATTACHMENT_DELETE_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "첨부파일 삭제에 실패했습니다."
    );

    private final HttpStatus httpStatus;
    private final String message;
}
