package com.comin.eapproval.chat.repository;

import com.comin.eapproval.chat.entity.ChatConversation;
import com.comin.eapproval.chat.entity.ChatConversationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * AI 대화방 데이터 접근 기능을 제공하는 Repository.
 */
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {

    /**
     * 사용자의 삭제되지 않은 대화방 개수를 조회한다.
     *
     * @param memberId 사용자 ID
     * @param status 제외할 대화방 상태
     * @return 삭제되지 않은 대화방 개수
     */
    long countByMemberIdAndStatusNot(Long memberId, ChatConversationStatus status);

    /**
     * 사용자가 소유한 활성 대화방을 조회한다.
     *
     * @param id 대화방 ID
     * @param memberId 사용자 ID
     * @param status 제외할 대화방 상태
     * @return 조건에 맞는 대화방
     */
    Optional<ChatConversation> findByIdAndMemberIdAndStatusNot(
            Long id,
            Long memberId,
            ChatConversationStatus status
    );

    /**
     * 사용자의 삭제되지 않은 대화방을 최근 메시지 순으로 조회한다.
     *
     * @param memberId 사용자 ID
     * @param status 제외할 대화방 상태
     * @return 사용자 소유 대화방 목록
     */
    List<ChatConversation> findByMemberIdAndStatusNotOrderByLastMessageAtDesc(
            Long memberId,
            ChatConversationStatus status
    );
}
