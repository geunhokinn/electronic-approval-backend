package com.comin.eapproval.chat.controller;

import com.comin.eapproval.chat.dto.request.ChatStreamingRequest;
import com.comin.eapproval.chat.service.ChatStreamingService;
import com.comin.eapproval.global.security.AiAuthorizationService;
import com.comin.eapproval.member.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 인증된 사용자의 AI 응답 스트리밍 API를 제공하는 Controller입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/messages")
public class ChatStreamingController {

    private final ChatStreamingService streamingService;
    private final AiAuthorizationService authorizationService;

    /** USER 메시지를 저장한 뒤 SSE 이벤트로 AI 응답을 전송합니다. */
    /**
     * USER 메시지를 저장한 뒤 SSE 이벤트로 AI 응답을 전송합니다.
     *
     * @param request 스트리밍 요청
     * @param userDetails 현재 인증된 사용자
     * @return SSE 이벤트 스트림
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> stream(
            @Valid @RequestBody ChatStreamingRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        authorizationService.requireAuthenticated(userDetails);
        return streamingService.stream(
                userDetails.getMemberId(),
                userDetails.getRole(),
                request
        );
    }
}
