package com.comin.eapproval.regulation.service;

import com.comin.eapproval.global.config.properties.QdrantProperties;
import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.regulation.RegulationErrorCode;
import lombok.RequiredArgsConstructor;
import com.comin.eapproval.regulation.dto.response.RegulationSearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/** 규정 임베딩을 Qdrant Point로 저장하는 서비스입니다. */
@Service
@RequiredArgsConstructor
public class RegulationQdrantService {

    private static final int VECTOR_DIMENSION = 1_024;

    private final QdrantProperties properties;
    private final ObjectMapper objectMapper;

    /** Qdrant 컬렉션을 생성하고 이미 존재하면 기존 컬렉션을 사용합니다. */
    public void ensureCollection() {
        try {
            client().put()
                    .uri("/collections/{collection}", properties.getCollection())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "vectors", Map.of(
                                    "size", VECTOR_DIMENSION,
                                    "distance", "Cosine"
                            )
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict ignored) {
            // 컬렉션이 이미 존재하면 별도 작업 없이 재사용합니다.
        }
    }

    /** 임베딩 벡터와 검색용 payload를 Qdrant Point로 저장합니다. */
    public void upsert(String pointId, List<Float> vector, Map<String, Object> payload) {
        client().put()
                .uri("/collections/{collection}/points", properties.getCollection())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "points", List.of(Map.of(
                                "id", pointId,
                                "vector", vector,
                                "payload", payload
                        ))
                ))
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * 질문 벡터와 유사한 규정 청크를 권한 조건과 함께 검색합니다.
     *
     * @param vector 질문 임베딩 벡터
     * @param limit 반환할 최대 결과 수
     * @param scoreThreshold 최소 유사도 점수
     * @return Qdrant 검색 결과
     */
    public List<RegulationSearchResult> search(
            List<Float> vector,
            int limit,
            double scoreThreshold
    ) {
        return search(vector, limit, scoreThreshold, null, null);
    }

    /** 질문에 포함된 조항·항 조건을 적용해 규정 청크를 검색합니다. */
    public List<RegulationSearchResult> search(
            List<Float> vector,
            int limit,
            double scoreThreshold,
            String articleNumber,
            String paragraphNumber
    ) {
        List<Map<String, Object>> conditions = new java.util.ArrayList<>();
        conditions.add(Map.of("key", "active", "match", Map.of("value", true)));
        if (articleNumber != null) {
            conditions.add(Map.of("key", "articleNumber", "match", Map.of("value", articleNumber)));
        }
        if (paragraphNumber != null) {
            conditions.add(Map.of("key", "paragraphNumber", "match", Map.of("value", paragraphNumber)));
        }
        Map<String, Object> filter = Map.of("must", conditions);
        String response = client().post()
                .uri("/collections/{collection}/points/search", properties.getCollection())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "vector", vector,
                        "filter", filter,
                        "limit", limit,
                        "score_threshold", scoreThreshold,
                        "with_payload", true
                ))
                .retrieve()
                .body(String.class);
        return parseSearchResults(response);
    }

    /**
     * 특정 규정에 연결된 모든 Point의 활성 상태를 갱신합니다.
     *
     * @param regulationId 규정 ID
     * @param active 변경할 활성 상태
     */
    public void updateActiveStatus(Long regulationId, boolean active) {
        try {
            client().post()
                .uri("/collections/{collection}/points/payload", properties.getCollection())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "payload", Map.of("active", active),
                        "filter", Map.of("must", List.of(
                                Map.of("key", "regulationId", "match", Map.of("value", regulationId))
                        ))
                ))
                .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            throw new BusinessException(RegulationErrorCode.REGULATION_SYNC_FAILED);
        }
    }

    private List<RegulationSearchResult> parseSearchResults(String response) {
        try {
            JsonNode results = objectMapper.readTree(response).path("result");
            List<RegulationSearchResult> searchResults = new java.util.ArrayList<>();
            for (JsonNode result : results) {
                JsonNode payload = result.path("payload");
                searchResults.add(new RegulationSearchResult(
                        result.path("id").asText(),
                        result.path("score").asDouble(),
                        payload.path("regulationId").isNumber() ? payload.path("regulationId").asLong() : null,
                        payload.path("regulationName").asText(null),
                        payload.path("category").asText(null),
                        payload.path("version").asText(null),
                        payload.path("effectiveDate").isTextual()
                                ? java.time.LocalDate.parse(payload.path("effectiveDate").asText())
                                : null,
                        payload.path("articleNumber").asText(null),
                        payload.path("articleTitle").asText(null),
                        payload.path("paragraphNumber").asText(null),
                        payload.path("content").asText(null)
                ));
            }
            return searchResults;
        } catch (IOException exception) {
            throw new BusinessException(RegulationErrorCode.REGULATION_SEARCH_FAILED);
        }
    }

    private RestClient client() {
        return RestClient.builder()
                .baseUrl("http://%s:%d".formatted(properties.getHost(), properties.getPort()))
                .build();
    }
}
