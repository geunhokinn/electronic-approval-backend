package com.comin.eapproval.chat.entity;

/**
 * AI 대화방의 상태를 정의하는 enum.
 */
public enum ChatConversationStatus {

    /** 사용 중인 대화방 */
    ACTIVE,

    /** 보관된 대화방 */
    ARCHIVED,

    /** 논리 삭제된 대화방 */
    DELETED
}
