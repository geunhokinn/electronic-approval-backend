package com.comin.eapproval.global.exception.notice;

import com.comin.eapproval.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 공지사항 도메인에서 발생하는 오류 코드를 정의한다.
 */
@Getter
@RequiredArgsConstructor
public enum NoticeErrorCode implements ErrorCode {

    NOTICE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "공지사항을 찾을 수 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String message;
}
