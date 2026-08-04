package com.comin.eapproval.attachment.controller;

import com.comin.eapproval.attachment.dto.response.AttachmentListResponse;
import com.comin.eapproval.attachment.dto.response.AttachmentUploadResponse;
import com.comin.eapproval.attachment.dto.response.AttachmentUrlResponse;
import com.comin.eapproval.attachment.service.AttachmentService;
import com.comin.eapproval.global.response.ApiResponse;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 문서 첨부파일 관련 API를 제공하는 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/documents/{documentId}/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    /**
     * 임시저장 문서에 첨부파일을 업로드한다.
     *
     * @param documentId 문서 ID
     * @param userDetails 로그인 사용자 정보
     * @param file 업로드 파일
     * @return 첨부파일 업로드 결과
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AttachmentUploadResponse>> uploadAttachment(
            @PathVariable Long documentId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("file") MultipartFile file
    ) {
        AttachmentUploadResponse response =
                attachmentService.uploadAttachment(
                        documentId,
                        userDetails.getMemberId(),
                        file
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "첨부파일을 업로드했습니다.",
                        response
                ));
    }

    /**
     * 문서에 포함된 첨부파일 목록을 조회한다.
     *
     * @param documentId 문서 ID
     * @param userDetails 로그인 사용자 정보
     * @return 첨부파일 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AttachmentListResponse>>> getAttachments(
            @PathVariable Long documentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<AttachmentListResponse> attachments =
                attachmentService.getAttachments(
                        documentId,
                        userDetails.getMemberId(),
                        MemberRole.valueOf(userDetails.getRole())
                );

        return ResponseEntity.ok(ApiResponse.success(attachments));
    }

    /**
     * 임시저장 문서에서 첨부파일을 삭제한다.
     *
     * @param documentId 문서 ID
     * @param attachmentId 첨부파일 ID
     * @param userDetails 로그인 사용자 정보
     * @return 첨부파일 삭제 결과
     */
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(
            @PathVariable Long documentId,
            @PathVariable Long attachmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        attachmentService.deleteAttachment(
                documentId,
                attachmentId,
                userDetails.getMemberId()
        );

        return ResponseEntity.ok(
                ApiResponse.success("첨부파일을 삭제했습니다.")
        );
    }

    /**
     * 첨부파일을 브라우저에서 미리 볼 수 있는 URL을 발급한다.
     *
     * @param documentId 문서 ID
     * @param attachmentId 첨부파일 ID
     * @param userDetails 로그인 사용자 정보
     * @return 첨부파일 미리보기 URL 정보
     */
    @GetMapping("/{attachmentId}/preview")
    public ResponseEntity<ApiResponse<AttachmentUrlResponse>> createPreviewUrl(
            @PathVariable Long documentId,
            @PathVariable Long attachmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AttachmentUrlResponse response = attachmentService.createPreviewUrl(
                documentId,
                attachmentId,
                userDetails.getMemberId(),
                MemberRole.valueOf(userDetails.getRole())
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 첨부파일을 원본 파일명으로 내려받을 수 있는 URL을 발급한다.
     *
     * @param documentId 문서 ID
     * @param attachmentId 첨부파일 ID
     * @param userDetails 로그인 사용자 정보
     * @return 첨부파일 다운로드 URL 정보
     */
    @GetMapping("/{attachmentId}/download")
    public ResponseEntity<ApiResponse<AttachmentUrlResponse>> createDownloadUrl(
            @PathVariable Long documentId,
            @PathVariable Long attachmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AttachmentUrlResponse response = attachmentService.createDownloadUrl(
                documentId,
                attachmentId,
                userDetails.getMemberId(),
                MemberRole.valueOf(userDetails.getRole())
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
