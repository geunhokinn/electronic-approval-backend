package com.comin.eapproval.global.security;

import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.security.SecurityErrorCode;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.security.CustomUserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * AI Tool 실행에 필요한 인증 사용자와 역할을 검증합니다.
 */
@Service
public class AiAuthorizationService {

    /**
     * 인증된 사용자인지 확인합니다.
     *
     * @param userDetails 현재 인증 사용자
     */
    public void requireAuthenticated(CustomUserDetails userDetails) {
        if (userDetails == null || !userDetails.isEnabled()) {
            throw new BusinessException(SecurityErrorCode.AUTHENTICATION_REQUIRED);
        }
    }

    /**
     * 사용자가 허용된 역할 중 하나인지 확인합니다.
     *
     * @param userDetails 현재 인증 사용자
     * @param allowedRoles 허용할 역할 목록
     */
    public void requireRole(CustomUserDetails userDetails, MemberRole... allowedRoles) {
        requireAuthenticated(userDetails);
        MemberRole currentRole = MemberRole.valueOf(userDetails.getRole());
        if (Arrays.stream(allowedRoles).noneMatch(currentRole::equals)) {
            throw new BusinessException(SecurityErrorCode.ACCESS_DENIED);
        }
    }

    /**
     * 관리자 전용 Tool 실행 권한을 확인합니다.
     *
     * @param userDetails 현재 인증 사용자
     */
    public void requireAdmin(CustomUserDetails userDetails) {
        requireRole(userDetails, MemberRole.ROLE_ADMIN);
    }

    /**
     * 결재자 또는 관리자 전용 Tool 실행 권한을 확인합니다.
     *
     * @param userDetails 현재 인증 사용자
     */
    public void requireApproverOrAdmin(CustomUserDetails userDetails) {
        requireRole(userDetails, MemberRole.ROLE_APPROVER, MemberRole.ROLE_ADMIN);
    }

    /**
     * 개인 대화방의 소유자인지 확인합니다.
     *
     * @param userDetails 현재 인증 사용자
     * @param ownerId 대화방 생성자 ID
     */
    public void requireOwner(CustomUserDetails userDetails, Long ownerId) {
        requireAuthenticated(userDetails);
        if (ownerId == null || !ownerId.equals(userDetails.getMemberId())) {
            throw new BusinessException(SecurityErrorCode.ACCESS_DENIED);
        }
    }
}
