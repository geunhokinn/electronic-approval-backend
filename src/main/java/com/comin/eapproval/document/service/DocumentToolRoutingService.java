package com.comin.eapproval.document.service;

import com.comin.eapproval.chat.dto.response.DocumentToolRouteResponse;
import com.comin.eapproval.document.dto.response.DocumentToolSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 문서 ID와 검색 결과를 상세 조회 Tool 호출 흐름으로 연결합니다.
 */
@Service
@RequiredArgsConstructor
public class DocumentToolRoutingService {

    /**
     * 문서 ID를 우선 사용하고, 없으면 검색 결과를 라우팅합니다.
     *
     * @param requestedDocumentId 사용자 질문에 포함된 문서 ID
     * @param contextDocumentId 최근 대화에서 확인된 문서 ID
     * @param searchResults 문서 검색 결과
     * @return 후속 Tool 호출 라우팅 결과
     */
    public DocumentToolRouteResponse route(
            Long requestedDocumentId,
            Long contextDocumentId,
            List<DocumentToolSearchResponse> searchResults
    ) {
        if (requestedDocumentId != null) {
            return DocumentToolRouteResponse.detail(requestedDocumentId);
        }
        if (contextDocumentId != null) {
            DocumentToolSearchResponse contextCandidate = searchResults == null
                    ? null
                    : searchResults.stream()
                    .filter(result -> contextDocumentId.equals(result.getDocumentId()))
                    .findFirst()
                    .orElse(null);
            if (contextCandidate != null) {
                return DocumentToolRouteResponse.confirm(contextCandidate);
            }
            return DocumentToolRouteResponse.confirm(
                    new DocumentToolSearchResponse(
                            contextDocumentId,
                            "이전 대화에서 확인된 문서",
                            null,
                            null,
                            null,
                            null,
                            null
                    )
            );
        }
        if (searchResults == null || searchResults.isEmpty()) {
            return DocumentToolRouteResponse.notFound();
        }
        if (searchResults.size() == 1) {
            return DocumentToolRouteResponse.confirm(searchResults.get(0));
        }
        return DocumentToolRouteResponse.select(searchResults);
    }
}
