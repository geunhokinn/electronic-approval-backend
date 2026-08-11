package com.comin.eapproval.regulation.service;

import com.comin.eapproval.global.config.properties.QdrantProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;

/** 규정 임베딩을 Qdrant Point로 저장하는 서비스입니다. */
@Service
@RequiredArgsConstructor
public class RegulationQdrantService {

    private static final int VECTOR_DIMENSION = 1_024;

    private final QdrantProperties properties;

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

    private RestClient client() {
        return RestClient.builder()
                .baseUrl("http://%s:%d".formatted(properties.getHost(), properties.getPort()))
                .build();
    }
}
