package com.comin.eapproval.regulation.service;

import com.comin.eapproval.chat.dto.response.AiToolResponse;
import com.comin.eapproval.chat.service.ChatToolContext;
import com.comin.eapproval.regulation.dto.response.RegulationListToolResponse;
import com.comin.eapproval.regulation.dto.response.RegulationDetailToolResponse;
import com.comin.eapproval.regulation.dto.response.RegulationSearchResult;
import com.comin.eapproval.regulation.dto.response.RegulationSearchToolResponse;
import com.comin.eapproval.regulation.entity.RegulationStatus;
import com.comin.eapproval.regulation.entity.RegulationChunk;
import com.comin.eapproval.regulation.repository.RegulationChunkRepository;
import com.comin.eapproval.regulation.repository.RegulationDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI가 호출할 수 있는 규정 조회 Tool을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegulationToolService {

    private static final Pattern ARTICLE_NUMBER_PATTERN = Pattern.compile("(?:제\\s*)?(\\d+)(?:\\s*조)?");
    private static final Pattern PARAGRAPH_NUMBER_PATTERN = Pattern.compile("(?:제\\s*)?(\\d+)(?:\\s*항)?");

    /** 활성 규정 목록의 최대 반환 개수입니다. */
    private static final int MAX_REGULATION_LIST_SIZE = 20;
    /** 규정 자체 조회 시 반환할 원문 최대 길이입니다. */
    private static final int MAX_REGULATION_OVERVIEW_LENGTH = 2000;
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
    사용자의 질문과 의미적으로 관련된 활성 규정 청크를 검색합니다.
    
    규정의 절차, 기준, 조건, 요건을 자연어로 질문했을 때 사용합니다.
    특정 조항 번호가 포함된 질문에는 사용하지 않고 getRegulation을 호출합니다.
    검색 결과에 포함된 내용만 근거로 답변하며, 결과에 없는 내용은 추정하지 않습니다.
    """)
        public AiToolResponse<List<RegulationSearchToolResponse>> searchRegulations(
            String query,
            ToolContext toolContext
    ) {
        log.info("searchRegulations Tool 호출");
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
    @Tool(description = """
    현재 활성화된 사내 규정의 목록과 각 규정의 ID, 규정명, 분류, 버전을 조회합니다.

    사용자가 등록된 규정이나 활성 규정 목록을 질문했을 때 사용합니다.
    특정 규정의 상세 조회가 필요하면 반환된 regulationId를 getRegulation에 전달합니다.
    """)
    public AiToolResponse<List<RegulationListToolResponse>> getRegulations(
            ToolContext toolContext
    ) {
        log.info("listActiveRegulations Tool 호출");
        ChatToolContext.memberId(toolContext);
        List<RegulationListToolResponse> results = regulationDocumentRepository
                .findByStatusOrderByTitleAscVersionDesc(RegulationStatus.ACTIVE)
                .stream()
                .limit(MAX_REGULATION_LIST_SIZE)
                .map(RegulationListToolResponse::from)
                .toList();

        log.info("listActiveRegulations Tool 결과 개수={}", results.size());
        return AiToolResponse.success(results);
    }

    private String normalizeNumber(String value, Pattern pattern, String suffix) {
        if (value == null || value.isBlank()) {
            return null;
        }
        Matcher matcher = pattern.matcher(value.trim());
        return matcher.find() ? "제" + matcher.group(1) + suffix : null;
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
