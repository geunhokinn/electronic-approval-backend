package com.comin.eapproval.member.repository;

import com.comin.eapproval.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

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

    /**
     * 로그인 아이디로 회원을 조회한다.
     *
     * @param username 로그인 아이디
     * @return 조회된 회원
     */
    Optional<Member> findByUsername(String username);
}
