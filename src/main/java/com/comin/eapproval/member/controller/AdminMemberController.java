package com.comin.eapproval.member.controller;

import com.comin.eapproval.global.response.ApiResponse;
import com.comin.eapproval.global.response.PageResponse;
import com.comin.eapproval.member.dto.response.MemberListResponse;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.entity.enums.MemberStatus;
import com.comin.eapproval.member.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자의 회원 관리 API를 제공하는 컨트롤러.
 *
 * 가입 대기 회원 조회, 회원가입 승인 및 거절,
 * 회원 권한 관리 등 관리자 기능을 처리한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    /**
     * 검색 조건에 따라 사용자 목록을 조회한다.
     *
     * @param status 회원 상태
     * @param role 회원 권한
     * @param keyword 사용자 이름 또는 로그인 아이디 검색어
     * @param pageable 페이징 정보
     * @return 사용자 목록 조회 결과
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MemberListResponse>>> getMembers(
            @RequestParam(required = false) MemberStatus status,
            @RequestParam(required = false) MemberRole role,
            @RequestParam(required = false) String keyword,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        PageResponse<MemberListResponse> members =
                adminMemberService.getMembers(
                        status,
                        role,
                        keyword,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "사용자 목록을 조회했습니다.",
                        members
                )
        );
    }
    
}
