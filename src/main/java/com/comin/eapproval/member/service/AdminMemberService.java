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

    
}