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
@RequestMapping("/api/admin/members")
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

    /**
     * 가입 대기 회원을 승인한다.
     *
     * @param memberId 회원 ID
     * @return 회원가입 승인 결과
     */
    @PatchMapping("/{memberId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveMember(
            @PathVariable Long memberId
    ) {
        adminMemberService.approveMember(memberId);

        return ResponseEntity.ok(
                ApiResponse.success("회원가입을 승인했습니다.")
        );
    }

    /**
     * 가입 대기 회원을 거절한다.
     *
     * @param memberId 회원 ID
     * @return 회원가입 거절 결과
     */
    @PatchMapping("/{memberId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectMember(
            @PathVariable Long memberId
    ) {
        adminMemberService.rejectMember(memberId);

        return ResponseEntity.ok(
                ApiResponse.success("회원가입을 거절했습니다.")
        );
    }

    /**
     * 사용자에게 결재 권한을 부여한다.
     *
     * @param memberId 회원 ID
     * @return 권한 부여 결과
     */
    @PatchMapping("/{memberId}/grant")
    public ResponseEntity<ApiResponse<Void>> grantApproverRole(
            @PathVariable Long memberId
    ) {
        adminMemberService.grantApproverRole(memberId);

        return ResponseEntity.ok(
                ApiResponse.success("결재 권한을 부여했습니다.")
        );
    }

    /**
     * 사용자의 결재 권한을 회수한다.
     *
     * @param memberId 회원 ID
     * @return 권한 회수 결과
     */
    @PatchMapping("/{memberId}/revoke")
    public ResponseEntity<ApiResponse<Void>> revokeApproverRole(
            @PathVariable Long memberId
    ) {
        adminMemberService.revokeApproverRole(memberId);

        return ResponseEntity.ok(
                ApiResponse.success("결재 권한을 회수했습니다.")
        );
    }

    /**
     * 회원을 비활성 상태로 변경한다.
     *
     * @param memberId 회원 ID
     * @return 회원 비활성화 결과
     */
    @PatchMapping("/{memberId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateMember(
            @PathVariable Long memberId
    ) {
        adminMemberService.deactivateMember(memberId);

        return ResponseEntity.ok(
                ApiResponse.success("회원을 비활성화했습니다.")
        );
    }

    /**
     * 비활성 회원을 다시 활성 상태로 변경한다.
     *
     * @param memberId 회원 ID
     * @return 회원 활성화 결과
     */
    @PatchMapping("/{memberId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateMember(
            @PathVariable Long memberId
    ) {
        adminMemberService.activateMember(memberId);

        return ResponseEntity.ok(
                ApiResponse.success("회원을 활성화했습니다.")
        );
    }

    /**
     * 사용자를 영구 삭제한다.
     *
     * @param memberId 회원 ID
     * @return 삭제 처리 결과
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(
            @PathVariable Long memberId
    ) {
        adminMemberService.deleteMember(memberId);

        return ResponseEntity.ok(
                ApiResponse.success("사용자를 삭제했습니다.")
        );
    }
}
