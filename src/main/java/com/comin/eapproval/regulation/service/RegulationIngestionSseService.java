package com.comin.eapproval.regulation.service;

import com.comin.eapproval.regulation.entity.RegulationIngestionStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 규정 AI 적재 상태를 관리자에게 SSE로 전달하는 서비스입니다. */
@Service
public class RegulationIngestionSseService {

    /** 규정별 적재 상태 SSE 연결입니다. */
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /** 규정 적재 상태 SSE 연결을 등록합니다. */
    public SseEmitter connect(Long regulationId, RegulationIngestionStatus status) {
        SseEmitter emitter = new SseEmitter(300_000L);
        SseEmitter previous = emitters.put(regulationId, emitter);
        if (previous != null) {
            previous.complete();
        }
        emitter.onCompletion(() -> emitters.remove(regulationId, emitter));
        emitter.onTimeout(() -> emitters.remove(regulationId, emitter));
        emitter.onError(error -> emitters.remove(regulationId, emitter));
        send(regulationId, status);
        return emitter;
    }

    /** 적재 상태 변경 이벤트를 전송합니다. */
    public void send(Long regulationId, RegulationIngestionStatus status) {
        SseEmitter emitter = emitters.get(regulationId);
        if (emitter == null) {
            return;
        }
        try {
            emitter.send(SseEmitter.event()
                    .name("ingestion-status")
                    .data(Map.of("regulationId", regulationId, "status", status.name())));
            if (status == RegulationIngestionStatus.COMPLETED
                    || status == RegulationIngestionStatus.FAILED) {
                emitters.remove(regulationId, emitter);
                emitter.complete();
            }
        } catch (IOException exception) {
            emitters.remove(regulationId, emitter);
            emitter.completeWithError(exception);
        }
    }
}
