package com.comin.eapproval.global.exception.document;

import com.comin.eapproval.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 문서 도메인에서 발생하는 오류 코드를 정의한다.
 */
@Getter
@RequiredArgsConstructor
public enum DocumentErrorCode implements ErrorCode {

    DOCUMENT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "문서를 찾을 수 없습니다."
    ),

    DOCUMENT_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "해당 문서를 조회할 권한이 없습니다."
    ),

    DOCUMENT_UPDATE_DENIED(
            HttpStatus.FORBIDDEN,
            "문서를 수정할 권한이 없습니다."
    ),

    DOCUMENT_NOT_EDITABLE(
            HttpStatus.CONFLICT,
            "임시저장 상태 또는 반려 상태 문서만 수정할 수 있습니다."
    ),

    DOCUMENT_NOT_DELETABLE(
            HttpStatus.CONFLICT,
            "임시저장 상태 문서만 삭제할 수 있습니다."
    ),

    DOCUMENT_DELETE_DENIED(
            HttpStatus.FORBIDDEN,
            "문서를 삭제할 권한이 없습니다."
    ),

    DOCUMENT_NOT_REQUESTABLE(
            HttpStatus.CONFLICT,
            "임시저장 상태의 문서만 결재를 요청할 수 있습니다."
    ),

    SELF_APPROVAL_NOT_ALLOWED(
            HttpStatus.BAD_REQUEST,
            "본인을 결재자로 지정할 수 없습니다."
    ),

    INVALID_APPROVER(
            HttpStatus.BAD_REQUEST,
            "결재 권한이 있는 회원만 결재자로 지정할 수 있습니다."
    );

    private final HttpStatus httpStatus;
    private final String message;
}
