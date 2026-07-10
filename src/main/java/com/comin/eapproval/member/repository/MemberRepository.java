package com.comin.eapproval.member.repository;

import com.comin.eapproval.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByUsername(String username);
}
