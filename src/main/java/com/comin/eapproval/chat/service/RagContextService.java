package com.comin.eapproval.chat.service;

import com.comin.eapproval.chat.dto.response.RagEvidence;
import com.comin.eapproval.regulation.dto.response.RegulationSearchResult;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Qdrant 검색 결과를 RAG 답변 생성용 근거 목록으로 정리하는 서비스.
 */
@Service
public class RagContextService {

    private static final double SCORE_THRESHOLD = 0.60;
    private static final int SIMPLE_EVIDENCE_LIMIT = 3;
    private static final int COMPLEX_EVIDENCE_LIMIT = 5;

    /** 검색 결과를 유사도와 중복 기준으로 정리한다. */
    public List<RagEvidence> prepare(
            List<RegulationSearchResult> results,
            boolean complexQuestion
    ) {
        int limit = complexQuestion ? COMPLEX_EVIDENCE_LIMIT : SIMPLE_EVIDENCE_LIMIT;
        Map<String, RegulationSearchResult> uniqueResults = new LinkedHashMap<>();

        results.stream()
                .filter(result -> result.getScore() >= SCORE_THRESHOLD)
                .filter(result -> result.getContent() != null && !result.getContent().isBlank())
                .sorted(Comparator.comparingDouble(RegulationSearchResult::getScore).reversed())
                .forEach(result -> uniqueResults.putIfAbsent(createEvidenceKey(result), result));

        return uniqueResults.values().stream()
                .limit(limit)
                .map(RagEvidence::from)
                .toList();
    }

    private String createEvidenceKey(RegulationSearchResult result) {
        return String.join("|",
                String.valueOf(result.getRegulationId()),
                String.valueOf(result.getVersion()),
                String.valueOf(result.getArticleNumber()),
                String.valueOf(result.getParagraphNumber())
        );
    }
}
