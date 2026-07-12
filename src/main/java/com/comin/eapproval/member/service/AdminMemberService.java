package com.comin.eapproval.member.service;

import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.member.MemberErrorCode;
import com.comin.eapproval.global.response.PageResponse;
import com.comin.eapproval.member.dto.response.MemberListResponse;
import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.entity.enums.MemberStatus;
import com.comin.eapproval.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 관리자의 회원 관리 비즈니스 로직을 처리하는 서비스.
 *
 * 가입 대기 회원 조회, 회원가입 승인 및 거절,
 * 회원 권한 관리 등 관리자 기능을 담당한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberService {

    private final MemberRepository memberRepository;

    /**
     * 검색 조건에 따라 사용자 목록을 조회한다.
     *
     * @param status 회원 상태
     * @param role 회원 권한
     * @param keyword 사용자 이름 또는 로그인 아이디 검색어
     * @param pageable 페이징 정보
     * @return 사용자 목록
     */
    public PageResponse<MemberListResponse> getMembers(
            MemberStatus status,
            MemberRole role,
            String keyword,
            Pageable pageable
    ) {
        Page<MemberListResponse> members = memberRepository
                .searchMembers(
                        status,
                        role,
                        keyword,
                        pageable
                )
                .map(MemberListResponse::from);

        return PageResponse.from(members);
    }

    /**
     * 회원가입을 승인한다.
     *
     * @param memberId 회원 ID
     */
    @Transactional
    public void approveMember(Long memberId) {
        Member member = findMember(memberId);
        validatePending(member);

        member.approve();
    }

    /**
     * 회원가입을 거절한다.
     *
     * @param memberId 회원 ID
     */
    @Transactional
    public void rejectMember(Long memberId) {
        Member member = findMember(memberId);
        validatePending(member);

        member.reject();
    }

    /**
     * 활성 사용자에게 결재 권한을 부여한다.
     *
     * @param memberId 회원 ID
     */
    @Transactional
    public void grantApproverRole(Long memberId) {
        Member member = findMember(memberId);

        validateActive(member);
        validateNotAdmin(member);
        validateApproverRoleNotGranted(member);

        member.grantApprover();
    }

    /**
     * 활성 사용자의 결재 권한을 회수한다.
     *
     * @param memberId 회원 ID
     */
    @Transactional
    public void revokeApproverRole(Long memberId) {
        Member member = findMember(memberId);

        validateActive(member);
        validateNotAdmin(member);
        validateApproverRoleGranted(member);

        member.revokeApprover();
    }

    /**
     * 활성 회원을 비활성 상태로 변경한다.
     *
     * @param memberId 회원 ID
     */
    @Transactional
    public void deactivateMember(Long memberId) {
        Member member = findMember(memberId);

        validateNotAdmin(member);
        validateActive(member);
        validateRevokedApproverRole(member);

        member.deactivate();
    }

    /**
     * 비활성 회원을 활성 상태로 변경한다.
     *
     * @param memberId 회원 ID
     */
    @Transactional
    public void activateMember(Long memberId) {
        Member member = findMember(memberId);

        validateNotAdmin(member);
        validateInactive(member);

        member.activate();
    }


    /**
     * private method
     */

    /**
     * 회원 ID로 회원을 조회한다.
     *
     * @param memberId 회원 ID
     * @return 회원
     */
    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND)
                );
    }

    /**
     * 가입 대기 상태인지 검증한다.
     *
     * @param member 검증 대상 회원
     */
    private void validatePending(Member member) {
        if (member.getStatus() != MemberStatus.PENDING) {
            throw new BusinessException(MemberErrorCode.MEMBER_NOT_PENDING);
        }
    }

    /**
     * 활성 상태인지 검증한다.
     *
     * @param member 검증 대상 회원
     */
    private void validateActive(Member member) {
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new BusinessException(MemberErrorCode.MEMBER_NOT_ACTIVE);
        }
    }

    /**
     * 관리자 계정이 아닌지 검증한다.
     *
     * @param member 검증 대상 회원
     */
    private void validateNotAdmin(Member member) {
        if (member.getRole() == MemberRole.ROLE_ADMIN) {
            throw new BusinessException(
                    MemberErrorCode.ADMIN_MEMBER_CANNOT_BE_MODIFIED
            );
        }
    }

    /**
     * 회원에게 결재 권한이 없는지 검증한다.
     *
     * @param member 검증 대상 회원
     */
    private void validateApproverRoleNotGranted(Member member) {
        if (member.getRole() == MemberRole.ROLE_APPROVER) {
            throw new BusinessException(
                    MemberErrorCode.APPROVER_ROLE_ALREADY_GRANTED
            );
        }
    }

    /**
     * 회원에게 결재 권한이 있는지 검증한다.
     *
     * @param member 검증 대상 회원
     */
    private void validateApproverRoleGranted(Member member) {
        if (member.getRole() != MemberRole.ROLE_APPROVER) {
            throw new BusinessException(
                    MemberErrorCode.APPROVER_ROLE_NOT_GRANTED
            );
        }
    }

    /**
     * 비활성 상태인지 검증한다.
     *
     * @param member 검증 대상 회원
     */
    private void validateInactive(Member member) {
        if (member.getStatus() != MemberStatus.INACTIVE) {
            throw new BusinessException(MemberErrorCode.MEMBER_NOT_INACTIVE);
        }
    }

    /**
     * 회원의 결재 권한이 회수되었는지 검증한다.
     *
     * @param member 검증 대상 회원
     */
    private void validateRevokedApproverRole(Member member) {
        if (member.getRole() == MemberRole.ROLE_APPROVER) {
            throw new BusinessException(
                    MemberErrorCode.APPROVER_ROLE_MUST_BE_REVOKED
            );
        }
    }

}