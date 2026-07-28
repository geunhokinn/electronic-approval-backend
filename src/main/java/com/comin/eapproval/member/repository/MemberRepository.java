package com.comin.eapproval.member.repository;

import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.entity.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
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

    /**
     * 지정한 상태와 권한에 해당하는 회원 목록을 조회한다.
     *
     * @param status 회원 상태
     * @param roles 회원 권한 목록
     * @return 조건에 해당하는 회원 목록
     */
    List<Member> findAllByStatusAndRoleIn(
            MemberStatus status,
            List<MemberRole> roles
    );
}
