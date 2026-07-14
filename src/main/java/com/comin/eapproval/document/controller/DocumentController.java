package com.comin.eapproval.document.controller;

import com.comin.eapproval.document.dto.request.DocumentCreateRequest;
import com.comin.eapproval.document.dto.response.DocumentCreateResponse;
import com.comin.eapproval.document.service.DocumentService;
import com.comin.eapproval.global.response.ApiResponse;
import com.comin.eapproval.member.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 문서 관련 API를 제공하는 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    /**
     * 로그인한 사용자의 문서를 임시저장한다.
     *
     * @param userDetails 로그인 사용자 정보
     * @param request 문서 임시저장 요청 정보
     * @return 문서 임시저장 결과
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DocumentCreateResponse>> createDraft(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody DocumentCreateRequest request
    ) {
        DocumentCreateResponse response =
                documentService.createDraft(
                        userDetails.getMemberId(),
                        request
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "문서를 임시저장했습니다.",
                                response
                        )
                );
    }
}
