package com.comin.eapproval.chat.service;

/**
 * AI Tool 호출에 적용할 공통 정책을 정의합니다.
 */
public final class ToolCallPolicy {

    /** 동일한 Tool을 한 요청에서 호출할 수 있는 최대 횟수입니다. */
    public static final int MAX_CALLS_PER_TOOL = 1;

    /** 한 사용자 요청에서 호출할 수 있는 전체 Tool의 최대 횟수입니다. */
    public static final int MAX_TOTAL_TOOL_CALLS = 5;

    /** 인스턴스 생성을 방지합니다. */
    private ToolCallPolicy() {
    }
}
