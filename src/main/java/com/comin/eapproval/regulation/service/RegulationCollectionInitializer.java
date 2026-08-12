package com.comin.eapproval.regulation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 규정 검색용 Qdrant 컬렉션을 준비하는 Component입니다.
 */
@Component
@RequiredArgsConstructor
public class RegulationCollectionInitializer {

    private final RegulationQdrantService regulationQdrantService;

    /**
     * 애플리케이션이 준비된 후 규정 컬렉션을 생성하거나 기존 컬렉션을 유지합니다.
     *
     * @param event 애플리케이션 준비 완료 이벤트
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initialize(ApplicationReadyEvent event) {
        regulationQdrantService.ensureCollection();
    }
}
