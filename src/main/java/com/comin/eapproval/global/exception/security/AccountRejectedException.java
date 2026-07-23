package com.comin.eapproval.global.exception.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 가입이 거부된 계정으로 로그인한 경우 발생하는 예외.
 */
public class AccountRejectedException extends AuthenticationException {

    public AccountRejectedException() {
        super(SecurityErrorCode.ACCOUNT_REJECTED.getMessage());
    }
}