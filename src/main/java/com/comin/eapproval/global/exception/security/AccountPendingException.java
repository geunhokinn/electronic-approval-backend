package com.comin.eapproval.global.exception.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 승인 대기 중인 계정으로 로그인한 경우 발생하는 예외.
 */
public class AccountPendingException extends AuthenticationException {

    public AccountPendingException() {
        super(SecurityErrorCode.ACCOUNT_PENDING.getMessage());
    }
}