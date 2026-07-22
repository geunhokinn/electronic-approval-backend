package com.comin.eapproval.member.controller;

import com.comin.eapproval.global.response.ApiResponse;
import com.comin.eapproval.member.dto.request.MemberSignupRequest;
import com.comin.eapproval.member.dto.response.CurrentMemberResponse;
import com.comin.eapproval.member.dto.response.MemberSignupResponse;
import com.comin.eapproval.member.security.CustomUserDetails;
import com.comin.eapproval.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 회원 관련 API를 제공하는 컨트롤러.
 *
 * 회원가입, 로그인, 로그아웃, 내 정보 조회 등
 * 일반 사용자의 기능을 처리한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입을 요청한다.
     *
     * @param request 회원가입 요청 정보
     * @return 회원가입 결과
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<MemberSignupResponse>> signup(
            @Valid @RequestBody MemberSignupRequest request) {

        MemberSignupResponse response = memberService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "회원가입 신청이 완료되었습니다. 관리자 승인 후 서비스를 이용할 수 있습니다.",
                        response
                ));
    }

    /**
     * 현재 로그인한 회원 정보를 조회한다.
     *
     * @param userDetails 로그인 사용자 정보
     * @return 현재 로그인 회원 정보
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentMemberResponse>> getCurrentMember(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        CurrentMemberResponse response =
                memberService.getCurrentMember(
                        userDetails.getMemberId()
                );

        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }
}
