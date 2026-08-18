package com.comin.eapproval.document.service;

import com.comin.eapproval.chat.dto.response.AiToolResponse;
import com.comin.eapproval.chat.service.ChatToolContext;
import com.comin.eapproval.document.dto.response.DocumentDetailResponse;
import com.comin.eapproval.document.dto.response.DocumentToolDetailResponse;
import com.comin.eapproval.document.dto.response.DocumentToolSearchResponse;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.comin.eapproval.document.repository.DocumentRepository;
import com.comin.eapproval.approval.repository.ApprovalRepository;
import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.document.DocumentErrorCode;
import com.comin.eapproval.member.entity.enums.MemberRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI가 호출할 수 있는 문서 조회 Tool을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentToolService {

    private static final int MAX_RESULT_SIZE = 10;

    private final DocumentRepository documentRepository;
    private final ApprovalRepository approvalRepository;

    /**
     * 로그인 사용자가 접근할 수 있는 문서를 조건으로 검색합니다.
     *
     * @param keyword 제목·본문 검색어
     * @param status 문서 상태 문자열
     * @return 문서 검색 결과
     */
    @Tool(description = "사용자가 조회할 수 있는 문서를 제목·본문 키워드와 상태로 검색합니다. 문서 ID가 없을 때 먼저 사용합니다.")
    public AiToolResponse<List<DocumentToolSearchResponse>> getDocuments(
            @ToolParam(required = false, description = "검색할 문서 제목 또는 본문 키워드입니다. 없으면 전체 문서를 검색합니다.")
            String keyword,
            @ToolParam(required = false, description = "문서 상태입니다. 없으면 모든 상태를 검색합니다.")
            String status,
            ToolContext toolContext
    ) {
        Long memberId = ChatToolContext.memberId(toolContext);
        String role = ChatToolContext.role(toolContext).name();
        log.info("getDocuments Tool 호출 - memberId={}, role={}, keyword={}, status={}",
                memberId, role, keyword, status);
        DocumentStatus documentStatus = parseStatus(status);
        if (status != null && !status.isBlank() && documentStatus == null) {
            log.warn("getDocuments 상태 파라미터 오류 - status={}", status);
            return AiToolResponse.failure("INVALID_DOCUMENT_STATUS", "지원하지 않는 문서 상태입니다.");
        }

        List<DocumentToolSearchResponse> results = documentRepository
                .searchAccessibleDocuments(
                        memberId,
                        role,
                        documentStatus,
                        keyword,
                        org.springframework.data.domain.PageRequest.of(0, MAX_RESULT_SIZE)
                )
                .getContent().stream()
                .limit(MAX_RESULT_SIZE)
                .map(DocumentToolSearchResponse::from)
                .toList();
        log.info("getDocuments Tool 결과 개수={}, documentIds={}",
                results.size(), results.stream()
                        .map(DocumentToolSearchResponse::getDocumentId)
                        .toList());
        return AiToolResponse.success(results);
    }

    /**
     * 사용자가 조회할 수 있는 문서의 상세 정보를 조회합니다.
     *
     * @param documentId 문서 식별자
     * @return 문서 상세 결과
     */
    @Tool(description = "문서 ID로 문서 상세 정보를 조회합니다. 사용자가 접근할 수 있는 문서만 반환합니다.")
    public AiToolResponse<DocumentToolDetailResponse> getDocument(
            Long documentId,
            ToolContext toolContext
    ) {
        log.info("getDocument Tool 호출 - documentId={}", documentId);
        if (documentId == null) {
            log.warn("getDocument 문서 ID 누락");
            return AiToolResponse.failure("INVALID_DOCUMENT_ID", "문서 ID가 필요합니다.");
        }

        Long memberId = ChatToolContext.memberId(toolContext);
        String role = ChatToolContext.role(toolContext).name();
        log.info("getDocument 접근 주체 - documentId={}, memberId={}, role={}",
                documentId, memberId, role);
        DocumentDetailResponse response = documentRepository
                .findDocumentDetailById(documentId)
                .orElseThrow(() -> new BusinessException(DocumentErrorCode.DOCUMENT_NOT_FOUND));

        MemberRole memberRole = ChatToolContext.role(toolContext);
        boolean isWriter = response.getWriterId().equals(memberId);
        boolean isAdmin = memberRole == MemberRole.ROLE_ADMIN;
        boolean isDraft = response.getStatus() == DocumentStatus.DRAFT;
        boolean isApprover = !isDraft
                && approvalRepository.existsByDocumentIdAndApproverId(documentId, memberId);
        if (!isWriter && !isApprover && !isAdmin) {
            log.warn("getDocument 접근 거부 - documentId={}, memberId={}, role={}",
                    documentId, memberId, role);
            throw new BusinessException(DocumentErrorCode.DOCUMENT_ACCESS_DENIED);
        }

        log.info("getDocument 상세 조회 완료 - documentId={}, isWriter={}, isApprover={}, isAdmin={}",
                documentId, isWriter, isApprover, isAdmin);
        return AiToolResponse.success(DocumentToolDetailResponse.from(response));
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
