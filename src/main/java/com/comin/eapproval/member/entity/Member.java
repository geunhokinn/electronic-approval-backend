package com.comin.eapproval.member.entity;

import com.comin.eapproval.global.entity.BaseEntity;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.entity.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String headquarters;

    @Column(nullable = false, length = 50)
    private String team;

    @Column(nullable = false, length = 50)
    private String position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MemberRole role;

    @Builder
    private Member(
            String username,
            String password,
            String name,
            String headquarters,
            String team,
            String position,
            MemberStatus status,
            MemberRole role
    ) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.headquarters = headquarters;
        this.team = team;
        this.position = position;
        this.status = status;
        this.role = role;
    }

    public static Member createEmployee(
            String username,
            String password,
            String name,
            String headquarters,
            String team,
            String position
    ) {
        return Member.builder()
                .username(username)
                .password(password)
                .name(name)
                .headquarters(headquarters)
                .team(team)
                .position(position)
                .status(MemberStatus.PENDING)
                .role(MemberRole.ROLE_EMPLOYEE)
                .build();
    }

    /**
     * 회원가입을 승인한다.
     */
    public void approve() {
        this.status = MemberStatus.ACTIVE;
    }

    /**
     * 회원가입을 거절한다.
     */
    public void reject() {
        this.status = MemberStatus.REJECTED;
    }

    /**
     * 결재 권한을 부여한다.
     */
    public void grantApprover() {
        this.role = MemberRole.ROLE_APPROVER;
    }

    /**
     * 결재 권한을 회수한다.
     */
    public void revokeApprover() {
        this.role = MemberRole.ROLE_EMPLOYEE;
    }

}

