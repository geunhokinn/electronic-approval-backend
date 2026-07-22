package com.comin.eapproval.member.service;

import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.member.MemberErrorCode;
import com.comin.eapproval.member.dto.request.MemberSignupRequest;
import com.comin.eapproval.member.dto.response.CurrentMemberResponse;
import com.comin.eapproval.member.dto.response.MemberSignupResponse;
import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입을 수행한다.
     *
     * @param request 회원가입 요청 정보
     * @return 회원가입 결과
     */
    @Transactional
    public MemberSignupResponse signup(MemberSignupRequest request) {
        validateDuplicateUsername(request.getUsername());

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = Member.createEmployee(
                request.getUsername(),
                encodedPassword,
                request.getName(),
                request.getHeadquarters(),
                request.getTeam(),
                request.getPosition()
        );

        Member savedMember = memberRepository.save(member);

        return MemberSignupResponse.of(savedMember.getId());
    }

    /**
     * 현재 로그인한 회원 정보를 조회한다.
     *
     * @param memberId 로그인 회원 ID
     * @return 현재 로그인 회원 정보
     */
    public CurrentMemberResponse getCurrentMember(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND)
                );

        return CurrentMemberResponse.from(member);
    }

    /**
     * 아이디 중복 여부를 검증한다.
     *
     * @param username 로그인 아이디
     */
    private void validateDuplicateUsername(String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new BusinessException(
                    MemberErrorCode.DUPLICATE_USERNAME
            );
        }
    }
}
