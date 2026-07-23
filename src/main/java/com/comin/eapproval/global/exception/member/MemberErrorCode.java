package com.comin.eapproval.global.exception.member;

import com.comin.eapproval.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    DUPLICATE_USERNAME(
            HttpStatus.CONFLICT,
            "이미 사용 중인 아이디입니다."
    ),

    MEMBER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "회원을 찾을 수 없습니다."
    ),

    MEMBER_NOT_PENDING(
            HttpStatus.CONFLICT,
            "가입 대기 상태의 사용자만 승인하거나 거절할 수 있습니다."
    ),

    MEMBER_NOT_ACTIVE(
            HttpStatus.CONFLICT,
            "활성 상태의 사용자만 권한을 변경할 수 있습니다."
    ),

    APPROVER_ROLE_ALREADY_GRANTED(
            HttpStatus.CONFLICT,
            "이미 결재 권한이 부여된 사용자입니다."
    ),

    APPROVER_ROLE_NOT_GRANTED(
            HttpStatus.CONFLICT,
            "결재 권한이 없는 사용자입니다."
    ),

    APPROVER_ROLE_MUST_BE_REVOKED(
            HttpStatus.CONFLICT,
            "결재 권한을 먼저 회수한 후 사용자를 비활성화할 수 있습니다."
    ),

    ADMIN_MEMBER_CANNOT_BE_MODIFIED(
            HttpStatus.FORBIDDEN,
            "관리자 계정은 해당 작업을 수행할 수 없습니다."
    ),

    MEMBER_CANNOT_BE_DELETED(
            HttpStatus.CONFLICT,
            "가입 대기 또는 가입 거절 상태의 사용자만 삭제할 수 있습니다."
    ),

    MEMBER_NOT_INACTIVE(
            HttpStatus.CONFLICT,
            "비활성 상태의 사용자만 다시 활성화할 수 있습니다."
    ),
    INVALID_HEADQUARTERS(
            HttpStatus.BAD_REQUEST,
            "허용되지 않은 본부입니다."
    ),

    INVALID_TEAM(
            HttpStatus.BAD_REQUEST,
            "선택한 팀이 해당 본부에 속하지 않습니다."
    ),

    INVALID_POSITION(
            HttpStatus.BAD_REQUEST,
            "허용되지 않은 직급입니다."
    );

    private final HttpStatus httpStatus;
    private final String message;
}
