package com.comin.eapproval.document.controller;

import com.comin.eapproval.document.dto.request.DocumentCreateRequest;
import com.comin.eapproval.document.dto.request.DocumentUpdateRequest;
import com.comin.eapproval.document.dto.response.DocumentCreateResponse;
import com.comin.eapproval.document.dto.response.DocumentDetailResponse;
import com.comin.eapproval.document.dto.response.DocumentListResponse;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.comin.eapproval.document.service.DocumentService;
import com.comin.eapproval.global.response.ApiResponse;
import com.comin.eapproval.global.response.PageResponse;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
            @Valid @RequestBody DocumentCreateRequest request) {

        DocumentCreateResponse response = documentService.createDraft(
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

    /**
     * 로그인 사용자가 작성한 문서 목록을 조회한다.
     *
     * @param userDetails 로그인 사용자 정보
     * @param status 문서 상태
     * @param keyword 제목 검색어
     * @param pageable 페이징 정보
     * @return 내 문서 목록 조회 결과
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DocumentListResponse>>> getMyDocuments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        PageResponse<DocumentListResponse> documents =
                documentService.getMyDocuments(
                        userDetails.getMemberId(),
                        status,
                        keyword,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "내 문서 목록을 조회했습니다.",
                        documents
                )
        );
    }

    /**
     * 문서 상세 정보를 조회한다.
     *
     * @param documentId 문서 ID
     * @param userDetails 로그인 사용자 정보
     * @return 문서 상세 조회 결과
     */
    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<DocumentDetailResponse>> getDocumentDetail(
            @PathVariable Long documentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DocumentDetailResponse document =
                documentService.getDocumentDetail(
                        documentId,
                        userDetails.getMemberId(),
                        MemberRole.valueOf(userDetails.getRole())
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "문서 상세 정보를 조회했습니다.",
                        document
                )
        );
    }

    /**
     * 로그인 사용자가 작성한 문서를 수정한다.
     *
     * @param documentId 문서 ID
     * @param userDetails 로그인 사용자 정보
     * @param request 문서 수정 요청 정보
     * @return 문서 수정 결과
     */
    @PatchMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Void>> updateDocument(
            @PathVariable Long documentId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody DocumentUpdateRequest request
    ) {
        documentService.updateDocument(
                documentId,
                userDetails.getMemberId(),
                request
        );

        return ResponseEntity.ok(
                ApiResponse.success("문서를 수정했습니다.")
        );
    }

    /**
     * 로그인 사용자가 작성한 문서를 삭제한다.
     *
     * 작성자 본인의 임시저장 문서만 삭제할 수 있다.
     *
     * @param documentId 문서 ID
     * @param userDetails 로그인 사용자 정보
     * @return 문서 삭제 결과
     */
    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable Long documentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        documentService.deleteDocument(
                documentId,
                userDetails.getMemberId()
        );

        return ResponseEntity.ok(
                ApiResponse.success("문서를 삭제했습니다.")
        );
    }
}
