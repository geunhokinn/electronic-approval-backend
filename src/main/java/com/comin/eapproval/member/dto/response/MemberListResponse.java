package com.comin.eapproval.member.dto.response;

import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.entity.enums.MemberStatus;
import lombok.Getter;

/**
 * 관리자 사용자 목록 조회 결과를 제공하는 응답 객체.
 */
@Getter
public class MemberListResponse {

    private final Long memberId;
    private final String username;
    private final String name;
    private final String headquarters;
    private final String team;
    private final String position;
    private final MemberStatus status;
    private final MemberRole role;

    private MemberListResponse(
            Long memberId,
            String username,
            String name,
            String headquarters,
            String team,
            String position,
            MemberStatus status,
            MemberRole role
    ) {
        this.memberId = memberId;
        this.username = username;
        this.name = name;
        this.headquarters = headquarters;
        this.team = team;
        this.position = position;
        this.status = status;
        this.role = role;
    }

    /**
     * Member 엔티티를 사용자 목록 응답으로 변환한다.
     *
     * @param member 회원 엔티티
     * @return 사용자 목록 응답
     */
    public static MemberListResponse from(Member member) {
        return new MemberListResponse(
                member.getId(),
                member.getUsername(),
                member.getName(),
                member.getHeadquarters(),
                member.getTeam(),
                member.getPosition(),
                member.getStatus(),
                member.getRole()
        );
    }
}
