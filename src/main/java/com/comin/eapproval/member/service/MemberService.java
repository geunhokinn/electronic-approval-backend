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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

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
