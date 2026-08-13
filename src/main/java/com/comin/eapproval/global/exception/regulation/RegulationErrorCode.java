package com.comin.eapproval.global.exception.regulation;

import com.comin.eapproval.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 규정 기능에서 발생하는 예외 코드를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum RegulationErrorCode implements ErrorCode {

    /** 동일한 규정 제목과 버전이 이미 존재하는 경우입니다. */
    REGULATION_DUPLICATE_VERSION(
            HttpStatus.CONFLICT,
            "동일한 규정 제목과 버전이 이미 존재합니다."
    ),

    /** 업로드 파일이 전달되지 않은 경우입니다. */
    REGULATION_FILE_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "규정 파일은 필수입니다."
    ),

    /** 규정 파일이 허용된 최대 크기를 초과한 경우입니다. */
    REGULATION_FILE_TOO_LARGE(
            HttpStatus.BAD_REQUEST,
            "규정 파일은 20MB 이하만 업로드할 수 있습니다."
    ),

    /** 허용하지 않는 확장자의 파일을 업로드한 경우입니다. */
    REGULATION_FILE_TYPE_NOT_ALLOWED(
            HttpStatus.BAD_REQUEST,
            "PDF, HWP, HWPX, TXT 파일만 업로드할 수 있습니다."
    ),

    /** S3 규정 원본 업로드에 실패한 경우입니다. */
    REGULATION_UPLOAD_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "규정 파일 업로드에 실패했습니다."
    ),

    /** Qdrant 규정 검색 응답을 해석하지 못한 경우입니다. */
    /** AI 적재가 완료되지 않은 규정을 활성화하려는 경우입니다. */
    REGULATION_NOT_READY(
            HttpStatus.BAD_REQUEST,
            "AI 적재가 완료된 규정만 활성화할 수 있습니다."
    ),

    REGULATION_SEARCH_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "규정 검색 중 오류가 발생했습니다."
    );

    private final HttpStatus httpStatus;
    private final String message;
}
