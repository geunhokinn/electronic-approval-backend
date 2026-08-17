package com.comin.eapproval.regulation.service;

import com.comin.eapproval.chat.dto.response.AiToolResponse;
import com.comin.eapproval.chat.service.ChatToolContext;
import com.comin.eapproval.regulation.dto.response.RegulationListToolResponse;
import com.comin.eapproval.regulation.dto.response.RegulationSearchResult;
import com.comin.eapproval.regulation.dto.response.RegulationSearchToolResponse;
import com.comin.eapproval.regulation.entity.RegulationStatus;
import com.comin.eapproval.regulation.repository.RegulationDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final Pattern ARTICLE_PATTERN = Pattern.compile("제\\s*(\\d+)\\s*조");
    private static final Pattern PARAGRAPH_PATTERN = Pattern.compile("제\\s*(\\d+)\\s*항");

    private final RegulationEmbeddingService embeddingService;
    private final RegulationQdrantService qdrantService;

    private final RegulationDocumentRepository regulationDocumentRepository;

    /**
     * 사용자 질문과 관련된 활성 규정 근거를 검색합니다.
     *
     * @param query 사용자가 입력한 규정 검색 질문
     * @return 검색 근거 또는 안전한 오류 응답
     */
    @Tool(description = "사용자 질문과 관련된 활성 사내 규정을 검색합니다. 특정 규정의 조항이나 기준을 확인할 때 사용합니다.")
    public AiToolResponse<List<RegulationSearchToolResponse>> searchRegulations(
            String query,
            ToolContext toolContext
    ) {
        ChatToolContext.memberId(toolContext);
        if (query == null || query.isBlank()) {
            return AiToolResponse.failure("INVALID_QUERY", "규정 검색어가 필요합니다.");
        }

        try {
            String articleNumber = extractNumber(query, ARTICLE_PATTERN, "조");
            String paragraphNumber = extractNumber(query, PARAGRAPH_PATTERN, "항");
            List<RegulationSearchToolResponse> results = qdrantService.search(
                            embeddingService.embed(query),
                            MAX_SEARCH_RESULT_SIZE,
                            SEARCH_SCORE_THRESHOLD,
                            articleNumber,
                            paragraphNumber
                    ).stream()
                    .map(this::toToolResponse)
                    .toList();

            if (articleNumber != null && results.stream().noneMatch(
                    result -> articleNumber.equals(result.getArticleNumber())
                            && (paragraphNumber == null
                            || paragraphNumber.equals(result.getParagraphNumber()))
            )) {
                return AiToolResponse.failure(
                        "REGULATION_ARTICLE_NOT_FOUND",
                        "요청한 조항을 현재 등록된 규정에서 찾을 수 없습니다."
                );
            }
            return AiToolResponse.success(results);
        } catch (IOException exception) {
            return AiToolResponse.failure("REGULATION_SEARCH_FAILED", "규정 검색을 완료하지 못했습니다.");
        }
    }

    /** 질문에서 조항 또는 항 번호를 추출하고 저장 형식으로 정규화합니다. */
    private String extractNumber(String query, Pattern pattern, String unit) {
        Matcher matcher = pattern.matcher(query);
        return matcher.find() ? "제" + matcher.group(1) + unit : null;
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
}
