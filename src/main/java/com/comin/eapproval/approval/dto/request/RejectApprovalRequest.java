package com.comin.eapproval.approval.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 결재 반려 요청 정보를 전달하는 DTO.
 */
@Getter
public class RejectApprovalRequest {

    @NotBlank(message = "반려 의견은 필수입니다.")
    private String comment;
}