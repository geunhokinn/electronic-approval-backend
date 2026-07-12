package com.comin.eapproval.member.service;

import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.member.MemberErrorCode;
import com.comin.eapproval.member.dto.request.MemberSignupRequest;
import com.comin.eapproval.member.dto.response.MemberSignupResponse;
import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스.
 *
 * 회원가입, 로그인, 로그아웃, 내 정보 조회 등
 * 일반 사용자의 기능을 담당한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입을 수행한다.
     *
     * @param request 회원가입 요청 정보
     * @return 회원가입 결과
     */
    @Transactional
    public MemberSignupResponse signup(MemberSignupRequest request) {
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(MemberErrorCode.DUPLICATE_USERNAME);
        }

        Member member = Member.createEmployee(
                request.getUsername(),
                request.getPassword(),
                request.getName(),
                request.getHeadquarters(),
                request.getTeam(),
                request.getPosition()
        );

        Member savedMember = memberRepository.save(member);

        return MemberSignupResponse.of(savedMember.getId());
    }
}
