package com.comin.eapproval.document.service;

import com.comin.eapproval.document.dto.request.DocumentDetailToolRequest;
import com.comin.eapproval.document.dto.response.DocumentDetailResponse;
import com.comin.eapproval.global.security.AiAuthorizationService;
import com.comin.eapproval.member.entity.enums.MemberRole;
import com.comin.eapproval.member.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * AI 문서 상세 조회 Tool 기능을 제공하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class DocumentDetailToolService {

    private final DocumentService documentService;
    private final AiAuthorizationService authorizationService;

    /**
     * 인증된 사용자의 문서 접근 권한을 확인한 뒤 문서 상세 정보를 조회한다.
     *
     * @param request 문서 상세 조회 조건
     * @param userDetails 현재 인증 사용자
     * @return 문서 상세 정보
     */
    public DocumentDetailResponse getDocumentDetail(
            DocumentDetailToolRequest request,
            CustomUserDetails userDetails
    ) {
        authorizationService.requireAuthenticated(userDetails);

        return documentService.getDocumentDetail(
                request.getDocumentId(),
                userDetails.getMemberId(),
                MemberRole.valueOf(userDetails.getRole())
        );
    }
}
