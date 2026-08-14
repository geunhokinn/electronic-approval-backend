package com.comin.eapproval.notice.controller;

import com.comin.eapproval.global.response.ApiResponse;
import com.comin.eapproval.global.response.PageResponse;
import com.comin.eapproval.notice.dto.response.NoticeDetailResponse;
import com.comin.eapproval.notice.dto.response.NoticeListResponse;
import com.comin.eapproval.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 공지사항 조회 API를 제공하는 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 목록을 조회한다.
     *
     * @param keyword 제목 검색어
     * @param isImportant 중요 공지 여부
     * @param pageable 페이징 정보
     * @return 공지사항 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NoticeListResponse>>> getNotices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isImportant,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        PageResponse<NoticeListResponse> notices = noticeService.getNotices(
                keyword,
                isImportant,
                pageable
        );

        return ResponseEntity.ok(
                ApiResponse.success(notices)
        );
    }

    /**
     * 공지사항 상세 정보를 조회한다.
     *
     * @param noticeId 공지사항 ID
     * @return 공지사항 상세 정보
     */
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeDetailResponse>> getNotice(
            @PathVariable Long noticeId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(noticeService.getNotice(noticeId))
        );
    }
}
