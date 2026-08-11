package com.comin.eapproval.approval.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 결재 정보 조회 Tool 요청 조건을 제공하는 DTO.
 */
@Getter
@AllArgsConstructor
public class ApprovalDetailToolRequest {

    @NotNull
    private Long approvalId;
}
