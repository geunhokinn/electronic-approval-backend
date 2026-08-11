package com.comin.eapproval.chat.controller;

import com.comin.eapproval.chat.dto.request.ChatMessageCreateRequest;
import com.comin.eapproval.chat.dto.response.ChatConversationResponse;
import com.comin.eapproval.chat.service.ChatConversationService;
import com.comin.eapproval.global.response.ApiResponse;
import com.comin.eapproval.global.security.AiAuthorizationService;
import com.comin.eapproval.member.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 대화방 관리 API를 제공하는 Controller.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/conversations")
public class ChatConversationController {

    private final ChatConversationService conversationService;
    private final AiAuthorizationService authorizationService;

    /**
     * 첫 메시지 전송 시 대화방을 생성한다.
     *
     * @param request 첫 메시지 요청
     * @param userDetails 현재 인증 사용자
     * @return 생성된 대화방
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ChatConversationResponse>> createConversation(
            @Valid @RequestBody ChatMessageCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        authorizationService.requireAuthenticated(userDetails);
        var conversation = conversationService.createWithFirstMessage(
                userDetails.getMemberId(),
                request
        );
        return ResponseEntity.ok(
                ApiResponse.success(conversationService.getConversation(
                        userDetails.getMemberId(), conversation.getId()
                ))
        );
    }

    /**
     * 현재 사용자의 대화방 목록을 조회한다.
     *
     * @param userDetails 현재 인증 사용자
     * @return 대화방 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatConversationResponse>>> getConversations(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        authorizationService.requireAuthenticated(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                conversationService.getConversations(userDetails.getMemberId())
        ));
    }

    /**
     * 대화방과 최근 메시지를 조회한다.
     *
     * @param conversationId 대화방 ID
     * @param userDetails 현재 인증 사용자
     * @return 대화방 상세 정보
     */
    @GetMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<ChatConversationResponse>> getConversation(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        authorizationService.requireAuthenticated(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                conversationService.getConversation(
                        userDetails.getMemberId(), conversationId
                )
        ));
    }

    /**
     * 대화방을 논리 삭제한다.
     *
     * @param conversationId 대화방 ID
     * @param userDetails 현재 인증 사용자
     * @return 처리 결과
     */
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        authorizationService.requireAuthenticated(userDetails);
        conversationService.deleteConversation(
                userDetails.getMemberId(), conversationId
        );
        return ResponseEntity.ok(ApiResponse.success("채팅방을 삭제했습니다."));
    }
}
