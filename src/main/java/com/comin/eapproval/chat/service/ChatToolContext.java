package com.comin.eapproval.chat.service;

import com.comin.eapproval.member.entity.enums.MemberRole;
import org.springframework.ai.chat.model.ToolContext;

/**
 * 서버가 인증된 사용자의 정보를 AI Tool 실행 컨텍스트에 전달하기 위한 유틸리티입니다.
 */
public final class ChatToolContext {

    /** ToolContext에 저장하는 회원 ID 키입니다. */
    public static final String MEMBER_ID = "memberId";

    /** ToolContext에 저장하는 회원 역할 키입니다. */
    public static final String ROLE = "role";

    private ChatToolContext() {
    }

    /**
     * ToolContext에서 서버가 주입한 회원 ID를 반환합니다.
     *
     * @param toolContext Spring AI Tool 실행 컨텍스트
     * @return 인증된 회원 ID
     * @throws IllegalArgumentException 컨텍스트에 인증 정보가 없을 때
     */
    public static Long memberId(ToolContext toolContext) {
        Object value = toolContext == null ? null : toolContext.getContext().get(MEMBER_ID);
        if (!(value instanceof Number number)) {
            throw new IllegalArgumentException("인증된 회원 정보가 없습니다.");
        }
        return number.longValue();
    }

    /**
     * ToolContext에서 서버가 주입한 회원 역할을 반환합니다.
     *
     * @param toolContext Spring AI Tool 실행 컨텍스트
     * @return 인증된 회원 역할
     * @throws IllegalArgumentException 역할 정보가 없거나 잘못된 경우
     */
    public static MemberRole role(ToolContext toolContext) {
        Object value = toolContext == null ? null : toolContext.getContext().get(ROLE);
        if (!(value instanceof String role)) {
            throw new IllegalArgumentException("인증된 역할 정보가 없습니다.");
        }
        try {
            return MemberRole.valueOf(role);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("유효하지 않은 역할 정보입니다.", exception);
        }
    }
}
