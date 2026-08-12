package com.comin.eapproval.chat.service;

import com.comin.eapproval.chat.dto.response.RagEvidence;
import com.comin.eapproval.chat.dto.response.RagSource;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * RAG 답변에 사용된 근거를 규정 출처 목록으로 변환하는 서비스.
 */
@Service
public class RagSourceService {

    /**
     * 중복 출처를 제거하고 사용자 표시용 DTO로 변환한다.
     *
     * @param evidences Nova에 전달된 규정 근거
     * @return 중복이 제거된 규정 출처
     */
    public List<RagSource> createSources(List<RagEvidence> evidences) {
        if (evidences == null || evidences.isEmpty()) {
            return List.of();
        }

        Map<String, RagEvidence> uniqueEvidences = new LinkedHashMap<>();
        evidences.forEach(evidence -> uniqueEvidences.putIfAbsent(
                createSourceKey(evidence),
                evidence
        ));

        return uniqueEvidences.values().stream()
                .map(RagSource::from)
                .toList();
    }

    private String createSourceKey(RagEvidence evidence) {
        return String.join("|",
                String.valueOf(evidence.getRegulationId()),
                String.valueOf(evidence.getVersion()),
                String.valueOf(evidence.getArticleNumber()),
                String.valueOf(evidence.getParagraphNumber())
        );
    }
}
