package com.comin.eapproval.global.exception.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 비활성화된 계정으로 로그인한 경우 발생하는 예외.
 */
public class AccountInactiveException extends AuthenticationException {

    public AccountInactiveException() {
        super(SecurityErrorCode.ACCOUNT_INACTIVE.getMessage());
    }
}