package com.comin.eapproval.chat.service;

import com.comin.eapproval.chat.dto.request.ChatMessageCreateRequest;
import com.comin.eapproval.chat.entity.ChatConversation;
import com.comin.eapproval.chat.entity.ChatConversationStatus;
import com.comin.eapproval.chat.entity.ChatMessage;
import com.comin.eapproval.chat.repository.ChatConversationRepository;
import com.comin.eapproval.chat.repository.ChatMessageRepository;
import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.chat.ChatErrorCode;
import com.comin.eapproval.chat.dto.response.ChatConversationResponse;
import com.comin.eapproval.chat.dto.response.ChatMessageResponse;
import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 대화방과 첫 사용자 메시지 저장 기능을 제공하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class ChatConversationService {

    private static final int MAX_CONVERSATION_COUNT = 10;
    private static final int RECENT_MESSAGE_LIMIT = 20;

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final MemberRepository memberRepository;

    /**
     * 첫 메시지 전송 시 대화방과 USER 메시지를 함께 생성한다.
     *
     * @param memberId 현재 로그인 사용자 ID
     * @param request 첫 메시지 저장 요청
     * @return 생성된 대화방
     */
    @Transactional
    public ChatConversation createWithFirstMessage(
            Long memberId,
            ChatMessageCreateRequest request
    ) {
        if (conversationRepository.countByMemberIdAndStatusNot(
                memberId,
                ChatConversationStatus.DELETED
        ) >= MAX_CONVERSATION_COUNT) {
            throw new BusinessException(ChatErrorCode.CHAT_CONVERSATION_LIMIT_EXCEEDED);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ChatErrorCode.CHAT_MEMBER_NOT_FOUND));

        ChatConversation conversation = ChatConversation.create(member, request.getMessage());
        conversationRepository.save(conversation);

        int sequence = conversation.nextMessageSequence();
        messageRepository.save(ChatMessage.userMessage(
                conversation,
                request.getRequestId(),
                request.getMessage().trim(),
                sequence
        ));
        conversation.updateLastMessageAt(LocalDateTime.now());

        return conversation;
    }

    /**
     * 사용자의 대화방 목록을 최근 메시지 순으로 조회한다.
     *
     * @param memberId 사용자 ID
     * @return 대화방 목록
     */
    @Transactional(readOnly = true)
    public List<ChatConversationResponse> getConversations(Long memberId) {
        return conversationRepository
                .findByMemberIdAndStatusNotOrderByLastMessageAtDesc(
                        memberId,
                        ChatConversationStatus.DELETED
                )
                .stream()
                .map(conversation -> ChatConversationResponse.from(conversation, Collections.emptyList()))
                .toList();
    }

    /**
     * 사용자의 대화방과 최근 메시지 20개를 조회한다.
     *
     * @param memberId 사용자 ID
     * @param conversationId 대화방 ID
     * @return 대화방 상세 정보
     */
    @Transactional(readOnly = true)
    public ChatConversationResponse getConversation(Long memberId, Long conversationId) {
        ChatConversation conversation = getOwnedConversation(memberId, conversationId);
        List<ChatMessageResponse> messages = messageRepository
                .findByConversationIdOrderBySequenceDesc(
                        conversationId,
                        org.springframework.data.domain.PageRequest.of(
                                0,
                                RECENT_MESSAGE_LIMIT
                        )
                )
                .stream()
                .sorted(java.util.Comparator.comparingInt(ChatMessage::getSequence))
                .map(ChatMessageResponse::from)
                .toList();
        return ChatConversationResponse.from(conversation, messages);
    }

    /**
     * 사용자가 소유한 대화방을 논리 삭제한다.
     *
     * @param memberId 사용자 ID
     * @param conversationId 대화방 ID
     */
    @Transactional
    public void deleteConversation(Long memberId, Long conversationId) {
        ChatConversation conversation = getOwnedConversation(memberId, conversationId);
        conversation.delete();
    }

    /**
     * AI 응답 메시지를 생성하고 처리 중 상태로 저장한다.
     *
     * @param memberId 사용자 ID
     * @param conversationId 대화방 ID
     * @param requestId 요청 식별자
     * @return 저장된 AI 응답 메시지
     */
    @Transactional
    public ChatMessage startAssistantMessage(
            Long memberId,
            Long conversationId,
            String requestId
    ) {
        ChatConversation conversation = getOwnedConversation(memberId, conversationId);
        ChatMessage message = ChatMessage.assistantMessage(
                conversation,
                requestId,
                conversation.nextMessageSequence()
        );
        message.startProcessing();
        messageRepository.save(message);
        conversation.updateLastMessageAt(LocalDateTime.now());
        return message;
    }

    /**
     * AI 응답 전체 내용과 모델 사용량을 저장한다.
     *
     * @param messageId AI 응답 메시지 ID
     * @param content 전체 응답 내용
     * @param model 사용 모델명
     * @param promptTokens 입력 토큰 수
     * @param completionTokens 출력 토큰 수
     * @param totalTokens 전체 토큰 수
     */
    @Transactional
    public void completeAssistantMessage(
            Long messageId,
            String content,
            String model,
            Integer promptTokens,
            Integer completionTokens,
            Integer totalTokens
    ) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException(ChatErrorCode.CHAT_CONVERSATION_NOT_FOUND));
        message.complete(content, model, promptTokens, completionTokens, totalTokens);
    }

    /** AI 응답을 실패 상태로 변경한다. */
    @Transactional
    public void failAssistantMessage(Long messageId, String errorCode) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException(ChatErrorCode.CHAT_CONVERSATION_NOT_FOUND));
        message.fail(errorCode);
    }

    /** 사용자가 중지한 AI 응답을 취소 상태로 변경한다. */
    @Transactional
    public void cancelAssistantMessage(Long messageId) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException(ChatErrorCode.CHAT_CONVERSATION_NOT_FOUND));
        message.cancel();
    }

    private ChatConversation getOwnedConversation(Long memberId, Long conversationId) {
        return conversationRepository
                .findByIdAndMemberIdAndStatusNot(
                        conversationId,
                        memberId,
                        ChatConversationStatus.DELETED
                )
                .orElseThrow(() -> new BusinessException(ChatErrorCode.CHAT_CONVERSATION_NOT_FOUND));
    }
}
