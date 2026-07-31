package com.comin.eapproval.member.dto.response;

import lombok.Getter;

/**
 * 아이디 사용 가능 여부 응답 DTO.
 */
@Getter
public class UsernameAvailabilityResponse {

    private final boolean available;

    private UsernameAvailabilityResponse(boolean available) {
        this.available = available;
    }

    /**
     * 아이디 사용 가능 여부 응답을 생성한다.
     *
     * @param available 사용 가능 여부
     * @return 아이디 사용 가능 여부 응답
     */
    public static UsernameAvailabilityResponse of(boolean available) {
        return new UsernameAvailabilityResponse(available);
    }
}
