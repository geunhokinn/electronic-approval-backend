package com.comin.eapproval.chat.service;

import com.comin.eapproval.chat.dto.request.ChatStreamingRequest;
import com.comin.eapproval.chat.dto.response.*;
import com.comin.eapproval.chat.entity.ChatMessage;
import com.comin.eapproval.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 인증된 사용자의 챗봇 요청을 SSE 이벤트 흐름으로 변환하는 Service입니다.
 */
@Service
@RequiredArgsConstructor
public class ChatStreamingService {

    private final ChatStreamingPreparationService preparationService;
    private final ChatConversationService conversationService;
    private final ChatGenerationService generationService;
    private final RagSourceService ragSourceService;
    private final ChatStreamingResponseService responseService;

    /** USER 저장 후 Nova 스트림을 SSE 이벤트로 전달합니다. */
    /**
     * USER 메시지를 저장한 뒤 Nova 스트림을 SSE 이벤트로 전달합니다.
     *
     * @param memberId 인증된 회원 ID
     * @param request 스트리밍 요청
     * @return SSE 이벤트 스트림
     */
    public Flux<ServerSentEvent<Object>> stream(Long memberId, String role, ChatStreamingRequest request) {
        ChatMessage userMessage;
        try {
            // 유저 정보 확인
            userMessage = preparationService.saveUserMessage(memberId, request);
        } catch (BusinessException exception) {
            return Flux.just(event("error", SseEvent.of(
                    SseEventType.ERROR,
                    null,
                    request.getRequestId(),
                    new SseErrorData(
                            exception.getErrorCode().toString(),
                            exception.getMessage(),
                            false
                    )
            )));
        }
        Long conversationId = userMessage.getConversation().getId();
        String requestId = request.getRequestId();
        // AI 대화 메시지 생성
        ChatMessage assistantMessage = conversationService.startAssistantMessage(
                memberId, conversationId, requestId
        );

        // 시작 이벤트 생성
        SseEvent<SseStartedData> started = SseEvent.of(
                SseEventType.STARTED,
                conversationId,
                requestId,
                new SseStartedData(userMessage.getId())
        );

        // 프론트엔드에 AI 응답 생성을 시작했습니다. 이벤트 보냄.
        return Flux.concat(
                Flux.just(event("started", started)),
                createResponseStream(memberId, role, request, assistantMessage)
        // Bedrock 호출이나 AI 처리 중 오류가 나면
        ).onErrorResume(error -> {
            responseService.fail(assistantMessage);
            SseEvent<SseErrorData> failed = SseEvent.of(
                    SseEventType.ERROR,
                    conversationId,
                    requestId,
                    new SseErrorData("CHAT_AI_RESPONSE_FAILED", "AI 응답을 생성하지 못했습니다.", true)
            );
            return Flux.just(event("error", failed));
        // 사용자가 중지한 경우
        }).doOnCancel(() -> responseService.cancel(assistantMessage));
    }

    /**
     * 질문 유형에 따라 일반 응답 또는 규정 기반 응답 스트림을 생성합니다.
     *
     * @param memberId 인증된 회원 ID
     * @param request 스트리밍 요청
     * @param assistantMessage 처리 중인 ASSISTANT 메시지
     * @return 상태·답변 조각·완료 이벤트 스트림
     */
    private Flux<ServerSentEvent<Object>> createResponseStream(
            Long memberId,
            String role,
            ChatStreamingRequest request,
            ChatMessage assistantMessage
    ) {
        // 채팅방, 요청 정보 확인
        Long conversationId = assistantMessage.getConversation().getId();
        String requestId = request.getRequestId();
        // Nova가 보내는 응답 조각을 임시 저장하는 목록
        List<ChatResponse> responses = new ArrayList<>();
        // AI 응답 스트림 생성
        Flux<ChatResponse> responseStream = createModelStream(memberId, role, request, conversationId);
        Flux<ServerSentEvent<Object>> deltas = responseStream
                // 응답 조각을 responses 목록에 저장
                .doOnNext(responses::add)
                // 응답 조각을 SSE delta 이벤트로 변환
                .map(response -> event("delta", SseEvent.of(
                        SseEventType.DELTA,
                        conversationId,
                        requestId,
                        // Nova의 ChatResponse를 프론트엔드가 이해할 수 있는 SSE 이벤트로 변환
                        new SseDeltaData(extractContent(response))
                )))
                // concatWith()는 앞의 responseStream 처리가 모두 끝난 뒤 실행
                .concatWith(Flux.defer(() -> {
                    // AI 최종 답변 저장
                    SseCompletedData completed = responseService.complete(
                            assistantMessage,
                            Flux.fromIterable(responses),
                            List.of()
                    );
                    // completed 이벤트 생성 후 프론트엔드에 전달
                    return Flux.just(event("completed", SseEvent.of(
                            SseEventType.COMPLETED,
                            conversationId,
                            requestId,
                            completed
                    )));
                }));

        // generating 상태 이벤트 먼저 전송
        return Flux.concat(
                Flux.just(event("status", SseEvent.of(
                        SseEventType.STATUS,
                        conversationId,
                        requestId,
                        new SseStatusData("GENERATING", "AI 응답을 생성하고 있습니다.")
                ))),
                deltas
        );
    }

    /**
     * 최근 대화 맥락을 구성하고 질문 유형에 맞는 모델 스트림을 선택합니다.
     *
     * @param memberId 인증된 회원 ID
     * @param request 스트리밍 요청
     * @param conversationId 대화방 ID
     * @return Nova 응답 스트림
     */
    private Flux<ChatResponse> createModelStream(
            Long memberId,
            String role,
            ChatStreamingRequest request,
            Long conversationId
    ) {
        // 최근 대화 조회
        List<ChatMessage> context = conversationService.getRecentMessagesForAi(memberId, conversationId);
        return generationService.stream(context, memberId, role);
    }

    /**
     * Spring AI 응답에서 답변 본문 조각을 추출합니다.
     *
     * @param response Spring AI 응답
     * @return 답변 본문 조각 또는 빈 문자열
     */
    private String extractContent(ChatResponse response) {
        if (response == null || response.getResult() == null
                || response.getResult().getOutput() == null) {
            return "";
        }
        return response.getResult().getOutput().getText();
    }

    /**
     * 공통 SSE 이벤트 객체를 Spring WebFlux 이벤트로 변환합니다.
     *
     * @param name 클라이언트가 구독할 이벤트 이름
     * @param data 전송할 이벤트 데이터
     * @return Spring SSE 이벤트
     */
    private ServerSentEvent<Object> event(String name, Object data) {
        return ServerSentEvent.<Object>builder()
                .event(name)
                .data(data)
                .build();
    }
}
