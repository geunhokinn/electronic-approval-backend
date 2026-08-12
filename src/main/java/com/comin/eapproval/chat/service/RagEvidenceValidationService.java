package com.comin.eapproval.chat.service;

import com.comin.eapproval.chat.dto.response.RagEvidence;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RAG 답변 생성 전에 규정 근거가 충분한지 검증하는 서비스.
 */
@Service
public class RagEvidenceValidationService {

    /**
     * Nova에 전달할 수 있는 근거가 하나 이상 있는지 확인한다.
     *
     * @param evidences 정리된 규정 근거 목록
     * @return 사용 가능한 근거가 있으면 true
     */
    public boolean hasUsableEvidence(List<RagEvidence> evidences) {
        return evidences != null
                && evidences.stream().anyMatch(this::isUsable);
    }

    private boolean isUsable(RagEvidence evidence) {
        return evidence != null
                && evidence.getContent() != null
                && !evidence.getContent().isBlank();
    }
}
