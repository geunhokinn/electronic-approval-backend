package com.comin.eapproval.member.security;

import com.comin.eapproval.global.exception.security.AccountInactiveException;
import com.comin.eapproval.global.exception.security.AccountPendingException;
import com.comin.eapproval.global.exception.security.AccountRejectedException;
import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 로그인 아이디를 이용하여 인증 사용자 정보를 조회한다.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * 로그인 아이디로 회원 정보를 조회한다.
     *
     * @param username 로그인 아이디
     * @return 인증 사용자 정보
     * @throws UsernameNotFoundException 회원을 찾을 수 없는 경우
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "회원을 찾을 수 없습니다."
                        )
                );

        switch (member.getStatus()) {
            case PENDING ->
                    throw new AccountPendingException();

            case INACTIVE ->
                    throw new AccountInactiveException();

            case REJECTED ->
                    throw new AccountRejectedException();

            default -> {
            }
        }

        return CustomUserDetails.from(member);
    }
}
