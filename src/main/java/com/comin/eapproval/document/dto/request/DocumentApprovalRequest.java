package com.comin.eapproval.document.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DocumentApprovalRequest {

    @NotNull(message = "결재자를 선택해주세요.")
    private Long approverId;
}
