package com.comin.eapproval.chat.repository;

import com.comin.eapproval.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * AI 메시지 데이터 접근 기능을 제공하는 Repository.
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 대화방과 요청 ID로 메시지를 조회한다.
     *
     * 동일 요청의 중복 처리를 방지하기 위해 사용한다.
     *
     * @param conversationId 대화방 ID
     * @param requestId 요청 식별자
     * @return 조건에 맞는 메시지
     */
    Optional<ChatMessage> findByConversationIdAndRequestId(
            Long conversationId,
            String requestId
    );
}
