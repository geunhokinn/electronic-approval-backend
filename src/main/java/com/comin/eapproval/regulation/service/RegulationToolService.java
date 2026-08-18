package com.comin.eapproval.regulation.service;

import com.comin.eapproval.chat.dto.response.AiToolResponse;
import com.comin.eapproval.chat.service.ChatToolContext;
import com.comin.eapproval.regulation.dto.response.RegulationListToolResponse;
import com.comin.eapproval.regulation.dto.response.RegulationSearchResult;
import com.comin.eapproval.regulation.dto.response.RegulationSearchToolResponse;
import com.comin.eapproval.regulation.entity.RegulationStatus;
import com.comin.eapproval.regulation.entity.RegulationChunk;
import com.comin.eapproval.regulation.repository.RegulationChunkRepository;
import com.comin.eapproval.regulation.repository.RegulationDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * AI가 호출할 수 있는 규정 조회 Tool을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class RegulationToolService {

    /** 활성 규정 목록의 최대 반환 개수입니다. */
    private static final int MAX_REGULATION_LIST_SIZE = 20;
    /** 규정 검색 결과의 최대 반환 개수입니다. */
    private static final int MAX_SEARCH_RESULT_SIZE = 5;
    /** 규정 검색 결과의 최소 유사도 점수입니다. */
    private static final double SEARCH_SCORE_THRESHOLD = 0.60;

    private final RegulationEmbeddingService embeddingService;
    private final RegulationQdrantService qdrantService;

    private final RegulationDocumentRepository regulationDocumentRepository;
    private final RegulationChunkRepository regulationChunkRepository;

    /**
     * 사용자 질문과 관련된 활성 규정 근거를 검색합니다.
     *
     * @param query 사용자가 입력한 규정 검색 질문
     * @return 검색 근거 또는 안전한 오류 응답
     */
    @Tool(description = """
        사용자의 자연어 질문과 의미적으로 관련된 활성 사내 규정 청크를 검색합니다.
        규정의 절차, 기준, 조건을 파악할 때 사용합니다.
        특정 조항 번호의 정확한 원문이나 전체 조항 목록 조회에는 사용하지 않습니다.
        """)
    public AiToolResponse<List<RegulationSearchToolResponse>> searchRegulations(
            String query,
            ToolContext toolContext
    ) {
        ChatToolContext.memberId(toolContext);
        if (query == null || query.isBlank()) {
            return AiToolResponse.failure("INVALID_QUERY", "규정 검색어가 필요합니다.");
        }

        try {
            List<RegulationSearchToolResponse> results = qdrantService.search(
                            embeddingService.embed(query),
                            MAX_SEARCH_RESULT_SIZE,
                            SEARCH_SCORE_THRESHOLD
                    ).stream()
                    .map(this::toToolResponse)
                    .toList();
            return AiToolResponse.success(results);
        } catch (IOException exception) {
            return AiToolResponse.failure("REGULATION_SEARCH_FAILED", "규정 검색을 완료하지 못했습니다.");
        }
    }

    /**
     * 현재 활성 상태인 규정 목록을 조회합니다.
     *
     * @return 활성 규정 목록 또는 안전한 오류 응답
     */
    @Tool(description = "현재 활성화된 사내 규정 목록을 조회합니다. 등록된 규정의 종류나 이름을 물을 때 사용합니다.")
    public AiToolResponse<List<RegulationListToolResponse>> listActiveRegulations(
            ToolContext toolContext
    ) {
        ChatToolContext.memberId(toolContext);
        List<RegulationListToolResponse> results = regulationDocumentRepository
                .findByStatusOrderByTitleAscVersionDesc(RegulationStatus.ACTIVE)
                .stream()
                .limit(MAX_REGULATION_LIST_SIZE)
                .map(RegulationListToolResponse::from)
                .toList();

        return AiToolResponse.success(results);
    }

    /**
     * 활성 규정에서 요청한 조항 또는 항의 내용을 조회합니다.
     *
     * @param regulationName 규정명
     * @param articleNumber 조항 번호
     * @param paragraphNumber 항 번호(선택)
     * @param toolContext AI Tool 실행 컨텍스트
     * @return 조회된 규정 청크 또는 안전한 오류 응답
     */
    @Tool(description = """
        활성화된 규정의 조항 또는 항을 조회합니다.
        사용자가 규정명과 제N조 또는 제N항의 내용을 물었을 때 사용합니다.
        조회 결과에 없는 내용은 추정하지 않습니다.
        """)
    public AiToolResponse<List<RegulationSearchToolResponse>> getRegulationArticle(
            String regulationName,
            String articleNumber,
            String paragraphNumber,
            ToolContext toolContext
    ) {
        ChatToolContext.memberId(toolContext);
        if (regulationName == null || regulationName.isBlank()
                || articleNumber == null || articleNumber.isBlank()) {
            return AiToolResponse.failure("INVALID_REGULATION_ARTICLE_QUERY", "규정명과 조항 번호가 필요합니다.");
        }

        List<RegulationSearchToolResponse> results = regulationChunkRepository
                .findByRegulationDocument_TitleAndRegulationDocument_StatusAndArticleNumberOrderByChunkIndexAsc(
                        regulationName.trim(),
                        RegulationStatus.ACTIVE,
                        articleNumber.trim()
                )
                .stream()
                .filter(chunk -> paragraphNumber == null || paragraphNumber.isBlank()
                        || paragraphNumber.trim().equals(chunk.getParagraphNumber()))
                .map(this::toArticleToolResponse)
                .toList();

        if (results.isEmpty()) {
            return AiToolResponse.failure("REGULATION_ARTICLE_NOT_FOUND", "요청한 활성 규정의 조항을 찾을 수 없습니다.");
        }
        return AiToolResponse.success(results);
    }

    /**
     * 기존 규정 검색 결과를 AI Tool 응답으로 변환합니다.
     *
     * @param result Qdrant 검색 결과
     * @return Tool 검색 결과
     */
    private RegulationSearchToolResponse toToolResponse(RegulationSearchResult result) {
        return new RegulationSearchToolResponse(
                result.getRegulationId(),
                result.getRegulationName(),
                result.getVersion(),
                result.getEffectiveDate(),
                result.getArticleNumber(),
                result.getArticleTitle(),
                result.getParagraphNumber(),
                result.getContent()
        );
    }

    private RegulationSearchToolResponse toArticleToolResponse(RegulationChunk chunk) {
        return new RegulationSearchToolResponse(
                chunk.getRegulationDocument().getId(),
                chunk.getRegulationDocument().getTitle(),
                chunk.getRegulationDocument().getVersion(),
                chunk.getRegulationDocument().getEffectiveDate(),
                chunk.getArticleNumber(),
                chunk.getArticleTitle(),
                chunk.getParagraphNumber(),
                chunk.getContent()
        );
    }
}
