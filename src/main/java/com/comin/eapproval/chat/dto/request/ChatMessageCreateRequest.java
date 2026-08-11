package com.comin.eapproval.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 첫 사용자 메시지 저장 요청 DTO.
 */
@Getter
@AllArgsConstructor
public class ChatMessageCreateRequest {

    @NotBlank
    @Size(max = 36)
    private String requestId;

    @NotBlank
    @Size(max = 1000)
    private String message;
}
