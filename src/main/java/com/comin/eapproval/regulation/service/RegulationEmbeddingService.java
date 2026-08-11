package com.comin.eapproval.regulation.service;

import com.comin.eapproval.global.config.properties.BedrockProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.io.IOException;
import java.util.List;

/**
 * Amazon Titan Text Embeddings V2로 규정 청크를 벡터화하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class RegulationEmbeddingService {

    private static final int VECTOR_DIMENSION = 1_024;

    private final BedrockRuntimeClient bedrockRuntimeClient;
    private final BedrockProperties bedrockProperties;
    private final ObjectMapper objectMapper;

    /**
     * 규정 청크 하나를 1,024차원 임베딩 벡터로 변환합니다.
     *
     * @param text 임베딩할 청크 원문
     * @return 임베딩 벡터
     * @throws IOException Bedrock 응답을 해석할 수 없는 경우
     */
    public List<Float> embed(String text) throws IOException {
        String requestBody = objectMapper.createObjectNode()
                .put("inputText", text)
                .put("dimensions", VECTOR_DIMENSION)
                .put("normalize", true)
                .toString();

        InvokeModelResponse response = bedrockRuntimeClient.invokeModel(
                InvokeModelRequest.builder()
                        .modelId(bedrockProperties.getEmbeddingModel())
                        .contentType("application/json")
                        .accept("application/json")
                        .body(SdkBytes.fromUtf8String(requestBody))
                        .build()
        );

        JsonNode embedding = objectMapper.readTree(response.body().asUtf8String())
                .path("embedding");
        if (!embedding.isArray() || embedding.size() != VECTOR_DIMENSION) {
            throw new IOException("Bedrock 임베딩 차원이 1,024와 일치하지 않습니다.");
        }

        List<Float> vector = new java.util.ArrayList<>(VECTOR_DIMENSION);
        embedding.forEach(value -> vector.add(value.floatValue()));
        return vector;
    }
}
