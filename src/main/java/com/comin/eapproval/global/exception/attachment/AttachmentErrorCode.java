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

    ATTACHMENT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "첨부파일을 찾을 수 없습니다."
    ),
    EMPTY_ATTACHMENT(
            HttpStatus.BAD_REQUEST,
            "빈 파일은 첨부할 수 없습니다."
    ),
    INVALID_ATTACHMENT_FILENAME(
            HttpStatus.BAD_REQUEST,
            "첨부파일 이름이 올바르지 않습니다."
    ),
    ATTACHMENT_FILENAME_TOO_LONG(
            HttpStatus.BAD_REQUEST,
            "첨부파일 이름은 255자 이하여야 합니다."
    ),
    ATTACHMENT_SIZE_EXCEEDED(
            HttpStatus.BAD_REQUEST,
            "첨부파일 크기는 10MB 이하여야 합니다."
    ),
    ATTACHMENT_COUNT_EXCEEDED(
            HttpStatus.BAD_REQUEST,
            "문서에는 첨부파일을 최대 3개까지 등록할 수 있습니다."
    ),
    INVALID_ATTACHMENT_TYPE(
            HttpStatus.BAD_REQUEST,
            "PDF, JPG, JPEG, PNG 파일만 첨부할 수 있습니다."
    ),

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
