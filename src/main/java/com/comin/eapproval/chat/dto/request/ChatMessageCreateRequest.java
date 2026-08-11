package com.comin.eapproval.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 첫 사용자 메시지 저장 요청 DTO.
 */
@Getter
@AllArgsConstructor
public class ChatMessageCreateRequest {

    @Positive
    private Long conversationId;

    @NotBlank
    @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
            message = "requestId는 UUID 형식이어야 합니다."
    )
    private String requestId;

    @NotBlank
    @Size(max = 1000)
    private String message;
}
