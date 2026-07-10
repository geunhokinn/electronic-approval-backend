package com.comin.eapproval.member.dto.response;

import lombok.Getter;

@Getter
public class MemberSignupResponse {

    private final Long memberId;

    private MemberSignupResponse(Long memberId) {
        this.memberId = memberId;
    }

    public static MemberSignupResponse of(Long memberId) {
        return new MemberSignupResponse(memberId);
    }
}
