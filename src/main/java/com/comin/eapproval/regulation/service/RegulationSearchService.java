package com.comin.eapproval.regulation.service;

import com.comin.eapproval.regulation.dto.response.RegulationSearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 사용자 질문과 관련된 규정 청크를 검색합니다.
 */
@Service
@RequiredArgsConstructor
public class RegulationSearchService {

    private static final int TOP_K = 5;
    private static final double SCORE_THRESHOLD = 0.60;

    private final RegulationEmbeddingService embeddingService;
    private final RegulationQdrantService qdrantService;

    /**
     * 사용자 역할에 허용된 규정만 Qdrant에서 검색합니다.
     *
     * @param question 검색 질문
     * @return 유사도가 높은 규정 청크 목록
     * @throws IOException 임베딩 응답을 해석할 수 없는 경우
     */
    public List<RegulationSearchResult> search(String question) throws IOException {
        if (question == null || question.isBlank()) {
            return List.of();
        }
        return qdrantService.search(
                embeddingService.embed(question),
                TOP_K,
                SCORE_THRESHOLD
        );
    }

}
