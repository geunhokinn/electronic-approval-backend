package com.comin.eapproval.chat.service;

import com.comin.eapproval.chat.dto.request.ChatMessageCreateRequest;
import com.comin.eapproval.chat.entity.ChatConversation;
import com.comin.eapproval.chat.entity.ChatConversationStatus;
import com.comin.eapproval.chat.entity.ChatMessage;
import com.comin.eapproval.chat.repository.ChatConversationRepository;
import com.comin.eapproval.chat.repository.ChatMessageRepository;
import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * AI 대화방과 첫 사용자 메시지 저장 기능을 제공하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class ChatConversationService {

    private static final int MAX_CONVERSATION_COUNT = 10;

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
            throw new IllegalStateException("채팅방은 사용자당 최대 10개까지 생성할 수 있습니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("회원을 찾을 수 없습니다."));

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
}
