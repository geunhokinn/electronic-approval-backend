package com.comin.eapproval.chat.entity;

/**
 * AI 대화 메시지의 발신 주체를 정의하는 enum.
 */
public enum ChatMessageRole {

    /** 사용자 메시지 */
    USER,

    /** AI 응답 메시지 */
    ASSISTANT,

    /** 시스템 메시지 */
    SYSTEM
}
