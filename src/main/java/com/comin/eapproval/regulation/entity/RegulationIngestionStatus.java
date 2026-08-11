package com.comin.eapproval.regulation.entity;

/**
 * 규정 원본 파일을 AI 검색 데이터로 변환하는 적재 처리 상태를 정의합니다.
 */
public enum RegulationIngestionStatus {
    /** 텍스트 추출과 벡터 적재를 기다리는 상태입니다. */
    PENDING,
    /** 텍스트 추출·청킹·임베딩을 처리 중인 상태입니다. */
    PROCESSING,
    /** AI 검색 데이터 적재가 완료된 상태입니다. */
    COMPLETED,
    /** AI 검색 데이터 적재에 실패한 상태입니다. */
    FAILED
}
