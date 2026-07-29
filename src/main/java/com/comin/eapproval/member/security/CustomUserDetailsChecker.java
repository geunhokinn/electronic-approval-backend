package com.comin.eapproval.member.security;

import com.comin.eapproval.global.exception.security.AccountInactiveException;
import com.comin.eapproval.global.exception.security.AccountPendingException;
import com.comin.eapproval.global.exception.security.AccountRejectedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

/**
 * 로그인 회원의 계정 상태를 검사한다.
 */
@Component
public class CustomUserDetailsChecker implements UserDetailsChecker {

    /**
     * 조회된 회원의 상태에 따라 인증 가능 여부를 확인한다.
     *
     * @param userDetails 인증 사용자 정보
     */
    @Override
    public void check(UserDetails userDetails) {
        CustomUserDetails customUserDetails =
                (CustomUserDetails) userDetails;

        switch (customUserDetails.getStatus()) {
            case PENDING -> throw new AccountPendingException();
            case INACTIVE -> throw new AccountInactiveException();
            case REJECTED -> throw new AccountRejectedException();
            default -> {
            }
        }
    }
}
