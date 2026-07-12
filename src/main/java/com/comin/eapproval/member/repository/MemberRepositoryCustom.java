package com.comin.eapproval.member.repository;

import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.entity.enums.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * QueryDSL을 이용한 회원 동적 조회 기능을 정의하는 Repository.
 */
public interface MemberRepositoryCustom {

    /**
     * 검색 조건에 따라 사용자 목록을 조회한다.
     *
     * @param status 회원 상태
     * @param role 회원 권한
     * @param keyword 사용자 이름 또는 로그인 아이디 검색어
     * @param pageable 페이징 정보
     * @return 조건에 맞는 사용자 목록
     */
    Page<Member> searchMembers(
            MemberStatus status,
            MemberRole role,
            String keyword,
            Pageable pageable
    );
}
