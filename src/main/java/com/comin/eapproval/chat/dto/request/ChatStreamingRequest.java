package com.comin.eapproval.chat.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI SSE 스트리밍 요청 DTO.
 */
@Getter
@NoArgsConstructor
public class ChatStreamingRequest {

    /** 기존 대화방 ID이며 첫 메시지이면 null입니다. */
    private Long conversationId;

    /** 클라이언트가 생성한 중복 요청 방지용 UUID입니다. */
    @NotBlank(message = "requestId는 필수입니다.")
    @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
            message = "requestId는 UUID 형식이어야 합니다."
    )
    private String requestId;

    /** AI에게 전달할 사용자 질문입니다. */
    @NotBlank(message = "메시지는 필수입니다.")
    @Size(min = 1, max = 1_000, message = "메시지는 1자 이상 1,000자 이하로 입력해야 합니다.")
    private String message;
}
