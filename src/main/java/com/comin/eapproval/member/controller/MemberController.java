package com.comin.eapproval.member.controller;

import com.comin.eapproval.global.response.ApiResponse;
import com.comin.eapproval.member.dto.request.MemberSignupRequest;
import com.comin.eapproval.member.dto.response.MemberSignupResponse;
import com.comin.eapproval.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

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
}
