package com.comin.eapproval.chat.dto.response;

/**
 * 문서 조회 Tool 라우팅 결과 유형입니다.
 */
public enum DocumentToolRouteType {

    /** 특정 문서의 상세 조회를 진행합니다. */
    DETAIL,

    /** 대화 맥락 또는 단일 검색 결과에 대한 사용자 확인이 필요합니다. */
    CONFIRM_REQUIRED,

    /** 검색 결과가 여러 건이므로 사용자 선택을 요청합니다. */
    SELECT_REQUIRED,

    /** 검색 결과가 없습니다. */
    NOT_FOUND
}
