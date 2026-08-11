package com.comin.eapproval.chat.entity;

/**
 * AI 메시지 처리 상태를 정의하는 enum.
 */
public enum ChatMessageStatus {

    /** 사용자 메시지를 접수한 상태 */
    RECEIVED,

    /** AI 메시지를 처리 중인 상태 */
    PROCESSING,

    /** AI 메시지 처리가 완료된 상태 */
    COMPLETED,

    /** AI 메시지 처리에 실패한 상태 */
    FAILED,

    /** 사용자가 AI 응답을 중지한 상태 */
    CANCELLED
}
