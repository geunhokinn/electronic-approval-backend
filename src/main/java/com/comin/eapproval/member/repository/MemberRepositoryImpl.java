package com.comin.eapproval.member.repository;

import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.entity.enums.MemberStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.comin.eapproval.member.entity.QMember.member;

/**
 * QueryDSL을 이용한 회원 동적 조회 기능을 구현하는 Repository.
 */
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 검색 조건에 따라 사용자 목록을 조회한다.
     *
     * @param status 회원 상태
     * @param role 회원 권한
     * @param keyword 사용자 이름 또는 로그인 아이디 검색어
     * @param pageable 페이징 정보
     * @return 조건에 맞는 사용자 목록
     */
    @Override
    public Page<Member> searchMembers(
            MemberStatus status,
            MemberRole role,
            String keyword,
            Pageable pageable
    ) {
        List<Member> content = queryFactory
                .selectFrom(member)
                .where(
                        statusEq(status),
                        roleEq(role),
                        keywordContains(keyword)
                )
                .orderBy(member.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(member.count())
                .from(member)
                .where(
                        statusEq(status),
                        roleEq(role),
                        keywordContains(keyword)
                )
                .fetchOne();

        return new PageImpl<>(
                content,
                pageable,
                total != null ? total : 0L
        );
    }

    /**
     * 회원 상태 검색 조건을 생성한다.
     *
     * @param status 회원 상태
     * @return 회원 상태 검색 조건
     */
    private BooleanExpression statusEq(MemberStatus status) {
        return status != null
                ? member.status.eq(status)
                : null;
    }

    /**
     * 회원 권한 검색 조건을 생성한다.
     *
     * @param role 회원 권한
     * @return 회원 권한 검색 조건
     */
    private BooleanExpression roleEq(MemberRole role) {
        return role != null
                ? member.role.eq(role)
                : null;
    }

    /**
     * 사용자 이름 또는 로그인 아이디 검색 조건을 생성한다.
     *
     * @param keyword 검색어
     * @return 이름 또는 로그인 아이디 검색 조건
     */
    private BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String trimmedKeyword = keyword.trim();

        return member.name.containsIgnoreCase(trimmedKeyword)
                .or(member.username.containsIgnoreCase(trimmedKeyword));
    }
}
