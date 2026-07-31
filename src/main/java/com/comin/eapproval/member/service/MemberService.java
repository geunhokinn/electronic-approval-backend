package com.comin.eapproval.member.service;

import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.member.MemberErrorCode;
import com.comin.eapproval.global.validator.OrganizationValidator;
import com.comin.eapproval.member.dto.request.MemberSignupRequest;
import com.comin.eapproval.member.dto.response.ApproverListResponse;
import com.comin.eapproval.member.dto.response.CurrentMemberResponse;
import com.comin.eapproval.member.dto.response.MemberSignupResponse;
import com.comin.eapproval.member.dto.response.UsernameAvailabilityResponse;
import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.entity.enums.MemberStatus;
import com.comin.eapproval.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private static final String USERNAME_PATTERN = "^[a-z0-9]{3,12}$";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationValidator organizationValidator;

    /**
     * 아이디 사용 가능 여부를 조회한다.
     *
     * @param username 확인할 아이디
     * @return 아이디 사용 가능 여부
     */
    public UsernameAvailabilityResponse checkUsernameAvailability(
            String username
    ) {
        boolean available = username.matches(USERNAME_PATTERN)
                && !memberRepository.existsByUsername(username);

        return UsernameAvailabilityResponse.of(available);
    }

    /**
     * 회원가입을 수행한다.
     *
     * @param request 회원가입 요청 정보
     * @return 회원가입 결과
     */
    @Transactional
    public MemberSignupResponse signup(MemberSignupRequest request) {

        organizationValidator.validate(
                request.getHeadquarters(),
                request.getTeam(),
                request.getPosition()
        );

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

    /**
     * 결재자 목록을 조회한다.
     *
     * @param memberId 로그인 회원 ID
     * @return 결재자 목록
     */
    public List<ApproverListResponse> getApprovers(Long memberId) {

        return memberRepository.findAllByStatusAndRoleIn(
                        MemberStatus.ACTIVE,
                        List.of(
                                MemberRole.ROLE_APPROVER,
                                MemberRole.ROLE_ADMIN
                        )
                )
                .stream()
                .filter(member -> !member.getId().equals(memberId))
                .map(ApproverListResponse::from)
                .toList();
    }
}
