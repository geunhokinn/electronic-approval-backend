package com.comin.eapproval.member.repository;

import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.entity.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 회원 데이터 접근 기능을 제공하는 Repository.
 */
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    /**
     * 아이디 중복 여부를 조회한다.
     *
     * @param username 아이디
     * @return 존재 여부
     */
    boolean existsByUsername(String username);
}
