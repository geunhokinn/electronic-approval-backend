package com.comin.eapproval.global.exception.approval;

import com.comin.eapproval.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 결재 도메인에서 발생하는 오류 코드를 정의한다.
 */
@Getter
@RequiredArgsConstructor
public enum ApprovalErrorCode implements ErrorCode {

    APPROVAL_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "결재 정보를 찾을 수 없습니다."
    ),

    NOT_APPROVER(
            HttpStatus.FORBIDDEN,
            "해당 문서를 결재할 권한이 없습니다."
    ),

    ALREADY_PROCESSED_APPROVAL(
            HttpStatus.CONFLICT,
            "이미 처리된 결재입니다."
    ),

    INVALID_APPROVAL_STATUS(
            HttpStatus.BAD_REQUEST,
            "유효하지 않은 결재 상태입니다."
    );

    private final HttpStatus httpStatus;
    private final String message;
}