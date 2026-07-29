package com.comin.eapproval.notice.controller;

import com.comin.eapproval.global.response.ApiResponse;
import com.comin.eapproval.member.security.CustomUserDetails;
import com.comin.eapproval.notice.dto.request.NoticeCreateRequest;
import com.comin.eapproval.notice.dto.request.NoticeUpdateRequest;
import com.comin.eapproval.notice.dto.response.NoticeCreateResponse;
import com.comin.eapproval.notice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 공지사항 관리 API를 제공하는 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/notices")
public class AdminNoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항을 등록한다.
     *
     * @param userDetails 로그인 관리자 정보
     * @param request 공지사항 등록 요청 정보
     * @return 공지사항 등록 결과
     */
    @PostMapping
    public ResponseEntity<ApiResponse<NoticeCreateResponse>> createNotice(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody NoticeCreateRequest request
    ) {
        NoticeCreateResponse response = noticeService.createNotice(
                userDetails.getMemberId(),
                request
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "공지사항을 등록했습니다.",
                        response
                ));
    }

    /**
     * 공지사항을 수정한다.
     *
     * @param noticeId 공지사항 ID
     * @param request 공지사항 수정 요청 정보
     * @return 공지사항 수정 결과
     */
    @PatchMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Void>> updateNotice(
            @PathVariable Long noticeId,
            @Valid @RequestBody NoticeUpdateRequest request
    ) {
        noticeService.updateNotice(noticeId, request);

        return ResponseEntity.ok(
                ApiResponse.success("공지사항을 수정했습니다.")
        );
    }

    /**
     * 공지사항을 삭제 처리한다.
     *
     * @param noticeId 공지사항 ID
     * @return 공지사항 삭제 결과
     */
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @PathVariable Long noticeId
    ) {
        noticeService.deleteNotice(noticeId);

        return ResponseEntity.ok(
                ApiResponse.success("공지사항을 삭제했습니다.")
        );
    }
}
