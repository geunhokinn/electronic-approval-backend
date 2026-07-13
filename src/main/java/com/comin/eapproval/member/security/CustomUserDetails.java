package com.comin.eapproval.member.security;

import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.entity.enums.MemberStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security 인증에 사용하는 회원 상세 정보를 제공한다.
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long memberId;
    private final String username;
    private final String password;
    private final String name;
    private final MemberStatus status;
    private final String role;

    private CustomUserDetails(
            Long memberId,
            String username,
            String password,
            String name,
            MemberStatus status,
            String role
    ) {
        this.memberId = memberId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.status = status;
        this.role = role;
    }

    /**
     * Member 엔티티를 인증 사용자 정보로 변환한다.
     *
     * @param member 회원 엔티티
     * @return 인증 사용자 정보
     */
    public static CustomUserDetails from(Member member) {
        return new CustomUserDetails(
                member.getId(),
                member.getUsername(),
                member.getPassword(),
                member.getName(),
                member.getStatus(),
                member.getRole().name()
        );
    }

    /**
     * 회원에게 부여된 권한을 반환한다.
     *
     * @return 회원 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    /**
     * 계정 만료 여부를 반환한다.
     *
     * @return 계정이 만료되지 않았으면 true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금 여부를 반환한다.
     *
     * @return 계정이 잠기지 않았으면 true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호 만료 여부를 반환한다.
     *
     * @return 비밀번호가 만료되지 않았으면 true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성 여부를 반환한다.
     *
     * @return ACTIVE 상태이면 true
     */
    @Override
    public boolean isEnabled() {
        return status == MemberStatus.ACTIVE;
    }
}
