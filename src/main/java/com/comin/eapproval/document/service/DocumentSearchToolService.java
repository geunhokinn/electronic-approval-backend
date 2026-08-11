package com.comin.eapproval.document.service;

import com.comin.eapproval.document.dto.request.DocumentSearchToolRequest;
import com.comin.eapproval.document.dto.response.DocumentListResponse;
import com.comin.eapproval.document.repository.DocumentRepository;
import com.comin.eapproval.global.security.AiAuthorizationService;
import com.comin.eapproval.member.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI의 전자결재 문서 검색 Tool을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class DocumentSearchToolService {

    private static final int MAX_RESULT_SIZE = 10;

    private final DocumentRepository documentRepository;
    private final AiAuthorizationService authorizationService;

    /**
     * 로그인 사용자의 문서 접근 범위에서 최대 10건을 검색합니다.
     *
     * @param request 검색 조건
     * @param userDetails 현재 인증 사용자
     * @return 권한이 확인된 문서 목록
     */
    public List<DocumentListResponse> search(
            DocumentSearchToolRequest request,
            CustomUserDetails userDetails
    ) {
        authorizationService.requireAuthenticated(userDetails);
        return documentRepository.searchAccessibleDocuments(
                userDetails.getMemberId(),
                userDetails.getRole(),
                request.getStatus(),
                request.getKeyword(),
                PageRequest.of(0, MAX_RESULT_SIZE)
        ).getContent();
    }
}
