package com.comin.eapproval.member.dto.response;

import com.comin.eapproval.member.entity.Member;
import lombok.Getter;

/**
 * 결재자 목록 조회 응답 DTO.
 */
@Getter
public class ApproverListResponse {

    private final Long memberId;
    private final String name;
    private final String headquarters;
    private final String team;
    private final String position;

    private ApproverListResponse(
            Long memberId,
            String name,
            String headquarters,
            String team,
            String position
    ) {
        this.memberId = memberId;
        this.name = name;
        this.headquarters = headquarters;
        this.team = team;
        this.position = position;
    }

    /**
     * 결재자 목록 응답 객체를 생성한다.
     *
     * @param member 결재자 회원
     * @return 결재자 목록 응답
     */
    public static ApproverListResponse from(Member member) {
        return new ApproverListResponse(
                member.getId(),
                member.getName(),
                member.getHeadquarters(),
                member.getTeam(),
                member.getPosition()
        );
    }
}