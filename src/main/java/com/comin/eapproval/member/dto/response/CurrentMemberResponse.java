package com.comin.eapproval.member.dto.response;

import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.entity.enums.MemberStatus;
import lombok.Getter;

/**
 * 현재 로그인한 회원 정보를 제공하는 응답 객체.
 */
@Getter
public class CurrentMemberResponse {

    private final Long memberId;
    private final String name;
    private final String headquarters;
    private final String team;
    private final String position;
    private final MemberStatus status;
    private final MemberRole role;

    private CurrentMemberResponse(
            Long memberId,
            String name,
            String headquarters,
            String team,
            String position,
            MemberStatus status,
            MemberRole role
    ) {
        this.memberId = memberId;
        this.name = name;
        this.headquarters = headquarters;
        this.team = team;
        this.position = position;
        this.status = status;
        this.role = role;
    }

    /**
     * Member 엔티티를 현재 로그인 회원 응답으로 변환한다.
     *
     * @param member 회원 엔티티
     * @return 현재 로그인 회원 응답
     */
    public static CurrentMemberResponse from(Member member) {
        return new CurrentMemberResponse(
                member.getId(),
                member.getName(),
                member.getHeadquarters(),
                member.getTeam(),
                member.getPosition(),
                member.getStatus(),
                member.getRole()
        );
    }
}