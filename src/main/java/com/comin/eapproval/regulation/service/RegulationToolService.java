package com.comin.eapproval.regulation.service;

import com.comin.eapproval.chat.dto.response.AiToolResponse;
import com.comin.eapproval.chat.service.ChatToolContext;
import com.comin.eapproval.regulation.dto.response.RegulationListToolResponse;
import com.comin.eapproval.regulation.dto.response.RegulationDetailToolResponse;
import com.comin.eapproval.regulation.dto.response.RegulationSearchResult;
import com.comin.eapproval.regulation.dto.response.RegulationSearchToolResponse;
import com.comin.eapproval.regulation.entity.RegulationStatus;
import com.comin.eapproval.regulation.entity.RegulationChunk;
import com.comin.eapproval.regulation.entity.RegulationDocument;
import com.comin.eapproval.regulation.repository.RegulationChunkRepository;
import com.comin.eapproval.regulation.repository.RegulationDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
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
    /** 규정 검색 결과의 최대 반환 개수입니다. */
    private static final int MAX_SEARCH_RESULT_SIZE = 5;
    /** 규정 검색 결과의 최소 유사도 점수입니다. */
    private static final double SEARCH_SCORE_THRESHOLD = 0.50;

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
            List<RegulationSearchResult> searchResults = qdrantService.search(
                    embeddingService.embed(query),
                    MAX_SEARCH_RESULT_SIZE,
                    SEARCH_SCORE_THRESHOLD
            );

            log.info("searchRegulations 검색 결과 - query={}, resultCount={}",
                    query, searchResults.size());
            searchResults.forEach(result -> log.info(
                    "searchRegulations 결과 - score={}, regulationId={}, regulationName={}, articleNumber={}, articleTitle={}, paragraphNumber={}, contentPreview={}",
                    result.getScore(),
                    result.getRegulationId(),
                    result.getRegulationName(),
                    result.getArticleNumber(),
                    result.getArticleTitle(),
                    result.getParagraphNumber(),
                    contentPreview(result.getContent())
            ));

            List<RegulationSearchToolResponse> results = searchResults.stream()
                    .map(this::toToolResponse)
                    .toList();
            return AiToolResponse.success(results);
        } catch (IOException exception) {
            return AiToolResponse.failure("REGULATION_SEARCH_FAILED", "규정 검색을 완료하지 못했습니다.");
        }
    }

    /** 로그에 기록할 규정 본문을 제한된 길이로 반환합니다. */
    private String contentPreview(String content) {
        if (content == null) {
            return null;
        }
        return content.length() <= 200 ? content : content.substring(0, 200) + "...";
    }

    /**
     * 현재 활성 상태인 규정 목록을 조회합니다.
     *
     * @return 활성 규정 목록 또는 안전한 오류 응답
     */
    @Tool(description = """
    현재 활성화된 사내 규정의 목록과 각 규정의 ID, 규정명, 분류, 버전,
    실제 포함된 조항 번호 목록과 조항 수를 조회합니다.
    사용자가 등록된 규정이나 활성 규정 목록을 질문했을 때 사용합니다.
    특정 규정의 상세 조회가 필요하면 반환된 regulationId를 getRegulation에 전달합니다.
    """)
    public AiToolResponse<List<RegulationListToolResponse>> getRegulations(
            ToolContext toolContext
    ) {
        log.info("getRegulations Tool 호출");
        ChatToolContext.memberId(toolContext);
        List<RegulationListToolResponse> results = regulationDocumentRepository
                .findByStatusOrderByTitleAscVersionDesc(RegulationStatus.ACTIVE)
                .stream()
                .limit(MAX_REGULATION_LIST_SIZE)
                .map(document -> {
                    List<String> articleNumbers = regulationChunkRepository
                            .findByRegulationDocumentIdOrderByIdAsc(document.getId())
                            .stream()
                            .map(RegulationChunk::getArticleNumber)
                            .filter(articleNumber -> articleNumber != null && !articleNumber.isBlank())
                            .distinct()
                            .toList();
                    return RegulationListToolResponse.from(document, articleNumbers);
                })
                .toList();

        log.info("getRegulations Tool 결과 개수={}, regulationIds={}",
                results.size(), results.stream()
                        .map(RegulationListToolResponse::getRegulationId)
                        .toList());
        return AiToolResponse.success(results);
    }

    /**
     * 활성 규정 목록에서 확인한 규정 ID를 기준으로 규정 상세 정보를 조회합니다.
     *
     * @param regulationId 목록 조회 결과의 규정 ID
     * @param articleNumber 선택한 조항 번호
     * @param paragraphNumber 선택한 항 번호
     * @param toolContext AI Tool 실행 컨텍스트
     * @return 규정 상세 정보 또는 조회 실패 응답
     */
    @Tool(description = """
    활성 규정의 특정 조항 또는 항 내용을 조회합니다.

    반드시 먼저 getRegulations를 호출하고, 그 결과의 regulationId를 사용합니다.
    articleNumber는 반드시 전달해야 하며, paragraphNumber는 항을 지정할 때만 전달합니다.
    조회 결과에 없는 내용은 추정하지 않습니다.
    """)
    public AiToolResponse<RegulationDetailToolResponse> getRegulation(
            @ToolParam(description = "getRegulations 결과의 regulationId입니다. 반드시 목록 조회 결과의 ID를 사용합니다.")
            Long regulationId,
            @ToolParam(description = "조회할 조항 번호입니다. 예: 제12조 또는 12")
            String articleNumber,
            @ToolParam(required = false, description = "조회할 항 번호입니다. 예: 제1항 또는 1")
            String paragraphNumber,
            ToolContext toolContext
    ) {
        log.info("getRegulation Tool 호출 - regulationId={}, articleNumber={}, paragraphNumber={}",
                regulationId, articleNumber, paragraphNumber);
        ChatToolContext.memberId(toolContext);

        if (articleNumber == null || articleNumber.isBlank()) {
            log.warn("getRegulation 조항 번호 누락 - regulationId={}", regulationId);
            return AiToolResponse.failure("INVALID_ARTICLE_NUMBER", "조회할 조항 번호가 필요합니다.");
        }

        RegulationDocument document = resolveActiveRegulation(regulationId);
        if (document == null) {
            log.warn("getRegulation 규정 조회 실패 - regulationId={}", regulationId);
            return AiToolResponse.failure("REGULATION_NOT_FOUND", "조회할 활성 규정을 찾을 수 없습니다.");
        }

        log.info("getRegulation 규정 해석 완료 - regulationId={}, title={}, status={}",
                document.getId(), document.getTitle(), document.getStatus());

        List<RegulationChunk> chunks = regulationChunkRepository
                .findByRegulationDocumentIdOrderByIdAsc(document.getId());
        if (chunks.isEmpty()) {
            log.warn("getRegulation 청크 없음 - regulationId={}", document.getId());
            return AiToolResponse.failure("REGULATION_CONTENT_NOT_FOUND", "규정의 적재된 내용을 찾을 수 없습니다.");
        }

        log.info("getRegulation 전체 청크 조회 완료 - regulationId={}, chunkCount={}",
                document.getId(), chunks.size());

        String normalizedArticle = normalizeNumber(articleNumber, ARTICLE_NUMBER_PATTERN, "조");
        String normalizedParagraph = normalizeNumber(paragraphNumber, PARAGRAPH_NUMBER_PATTERN, "항");
        List<RegulationChunk> selected = chunks.stream()
                .filter(chunk -> normalizedArticle == null || normalizedArticle.equals(chunk.getArticleNumber()))
                .filter(chunk -> normalizedParagraph == null || normalizedParagraph.equals(chunk.getParagraphNumber()))
                .toList();
        if ((normalizedArticle != null || normalizedParagraph != null) && selected.isEmpty()) {
            log.warn("getRegulation 조항 매칭 실패 - regulationId={}, articleNumber={}, paragraphNumber={}",
                    document.getId(), normalizedArticle, normalizedParagraph);
            return AiToolResponse.failure("REGULATION_ARTICLE_NOT_FOUND", "요청한 조항 또는 항을 찾을 수 없습니다.");
        }

        log.info("getRegulation 조항 매칭 결과 - regulationId={}, articleNumber={}, paragraphNumber={}, selectedCount={}",
                document.getId(), normalizedArticle, normalizedParagraph, selected.size());

        String content = selected.stream()
                .map(chunk -> {
                    String paragraphNum = chunk.getParagraphNumber();
                    if (paragraphNum == null
                            || paragraphNum.isBlank()
                            || "없음".equals(paragraphNum)) {
                        return chunk.getContent();
                    }
                    return "[" + paragraphNum + "]\n" + chunk.getContent();
                })
                .collect(Collectors.joining("\n\n"));
        String articleTitle = selected.stream()
                .map(RegulationChunk::getArticleTitle)
                .filter(title -> title != null && !title.isBlank())
                .findFirst()
                .orElse(null);
        return AiToolResponse.success(new RegulationDetailToolResponse(
                document.getId(), document.getTitle(), document.getCategory().name(),
                document.getVersion(), document.getEffectiveDate(), normalizedArticle,
                articleTitle, normalizedParagraph, content));
    }

    private RegulationDocument resolveActiveRegulation(Long regulationId) {
        if (regulationId == null) {
            return null;
        }
        return regulationDocumentRepository.findById(regulationId)
                .filter(document -> document.getStatus() == RegulationStatus.ACTIVE)
                .orElse(null);
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
