package com.comin.eapproval.document.service;

import com.comin.eapproval.chat.dto.response.AiToolResponse;
import com.comin.eapproval.document.dto.request.DocumentSearchToolRequest;
import com.comin.eapproval.document.dto.response.DocumentDetailResponse;
import com.comin.eapproval.document.dto.response.DocumentToolDetailResponse;
import com.comin.eapproval.document.dto.response.DocumentToolSearchResponse;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.comin.eapproval.global.security.AiAuthorizationService;
import com.comin.eapproval.member.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI가 호출할 수 있는 문서 조회 Tool을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class DocumentToolService {

    private static final int MAX_RESULT_SIZE = 10;

    private final DocumentSearchToolService documentSearchToolService;
    private final DocumentDetailToolService documentDetailToolService;
    private final AiAuthorizationService authorizationService;

    /**
     * 로그인 사용자가 접근할 수 있는 문서를 조건으로 검색합니다.
     *
     * @param keyword 제목·본문 검색어
     * @param status 문서 상태 문자열
     * @return 문서 검색 결과
     */
    @Tool(description = "사용자가 조회할 수 있는 문서를 제목·본문 키워드와 상태로 검색합니다. 문서 ID가 없을 때 먼저 사용합니다.")
    public AiToolResponse<List<DocumentToolSearchResponse>> searchDocuments(
            String keyword,
            String status
    ) {
        CustomUserDetails userDetails = currentUser();
        DocumentStatus documentStatus = parseStatus(status);
        if (status != null && !status.isBlank() && documentStatus == null) {
            return AiToolResponse.failure("INVALID_DOCUMENT_STATUS", "지원하지 않는 문서 상태입니다.");
        }

        List<DocumentToolSearchResponse> results = documentSearchToolService.search(
                        new DocumentSearchToolRequest(keyword, documentStatus),
                        userDetails
                ).stream()
                .limit(MAX_RESULT_SIZE)
                .map(DocumentToolSearchResponse::from)
                .toList();
        return AiToolResponse.success(results);
    }

    /**
     * 사용자가 조회할 수 있는 문서의 상세 정보를 조회합니다.
     *
     * @param documentId 문서 식별자
     * @return 문서 상세 결과
     */
    @Tool(description = "문서 ID로 문서 상세 정보를 조회합니다. 사용자가 접근할 수 있는 문서만 반환합니다.")
    public AiToolResponse<DocumentToolDetailResponse> getDocument(Long documentId) {
        if (documentId == null) {
            return AiToolResponse.failure("INVALID_DOCUMENT_ID", "문서 ID가 필요합니다.");
        }

        CustomUserDetails userDetails = currentUser();
        DocumentDetailResponse response = documentDetailToolService.getDocumentDetail(
                new com.comin.eapproval.document.dto.request.DocumentDetailToolRequest(documentId),
                userDetails
        );
        return AiToolResponse.success(DocumentToolDetailResponse.from(response));
    }

    /**
     * 현재 인증된 사용자를 조회합니다.
     *
     * @return 현재 인증 사용자
     */
    private CustomUserDetails currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            authorizationService.requireAuthenticated(null);
        }
        return (CustomUserDetails) authentication.getPrincipal();
    }

    /**
     * 문자열 상태를 문서 상태 enum으로 변환합니다.
     *
     * @param status 문서 상태 문자열
     * @return 변환된 상태 또는 null
     */
    private DocumentStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return DocumentStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
