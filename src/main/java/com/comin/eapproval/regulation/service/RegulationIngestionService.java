package com.comin.eapproval.regulation.service;

import com.comin.eapproval.regulation.dto.RegulationTextChunk;
import com.comin.eapproval.regulation.entity.RegulationChunk;
import com.comin.eapproval.regulation.entity.RegulationDocument;
import com.comin.eapproval.regulation.entity.RegulationIngestionStatus;
import com.comin.eapproval.regulation.repository.RegulationChunkRepository;
import com.comin.eapproval.regulation.repository.RegulationDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * 규정 원본을 추출·청킹·임베딩하고 적재 상태를 자동으로 관리합니다.
 */
@Service
@RequiredArgsConstructor
public class RegulationIngestionService {

    private final RegulationDocumentRepository documentRepository;
    private final RegulationChunkRepository chunkRepository;
    private final RegulationStorageService storageService;
    private final RegulationTextExtractor textExtractor;
    private final RegulationChunkingService chunkingService;
    private final RegulationEmbeddingService embeddingService;
    private final RegulationQdrantService regulationQdrantService;
    private final RegulationIngestionSseService sseService;

    /**
     * 규정 적재 작업을 비동기로 시작합니다.
     *
     * @param regulationId 적재할 규정 ID
     */
    @Async
    @Transactional
    public void ingestAsync(Long regulationId) {
        sseService.send(regulationId, RegulationIngestionStatus.PROCESSING);
        try {
            process(regulationId);
        } catch (Exception exception) {
            updateStatus(regulationId, RegulationIngestionStatus.FAILED);
            sseService.send(regulationId, RegulationIngestionStatus.FAILED);
        }
    }

    @Transactional
    protected void process(Long regulationId) throws IOException {
        RegulationDocument document = findDocument(regulationId);
        document.updateIngestionStatus(RegulationIngestionStatus.PROCESSING);

        String markdown = textExtractor.extract(
                storageService.download(document.getObjectKey()),
                document.getOriginalFilename()
        );
        document.updateContent(markdown);
        regulationQdrantService.ensureCollection();

        for (RegulationTextChunk textChunk : chunkingService.chunk(
                markdown,
                document.getCategory().name()
        )) {
            String pointId = UUID.randomUUID().toString();
            RegulationChunk chunk = RegulationChunk.create(
                    document,
                    textChunk.getChunkIndex(),
                    textChunk.getContent()
            );
            chunk.updateVectorId(pointId);
            chunkRepository.save(chunk);
            regulationQdrantService.upsert(
                    pointId,
                    embeddingService.embed(textChunk.getEmbeddingText()),
                    Map.ofEntries(
                            Map.entry("regulationId", document.getId()),
                            Map.entry("regulationName", document.getTitle()),
                            Map.entry("category", document.getCategory().name()),
                            Map.entry("version", document.getVersion()),
                            Map.entry("effectiveDate", document.getEffectiveDate().toString()),
                            Map.entry("active", document.getStatus().name().equals("ACTIVE")),
                            Map.entry("articleNumber", textChunk.getArticleNumber()),
                            Map.entry("articleTitle", textChunk.getArticleTitle()),
                            Map.entry("paragraphNumber", textChunk.getParagraphNumber()),
                            Map.entry("chunkIndex", textChunk.getChunkIndex()),
                            Map.entry("content", textChunk.getContent())
                    )
            );
        }
        document.updateIngestionStatus(RegulationIngestionStatus.COMPLETED);
        sseService.send(regulationId, RegulationIngestionStatus.COMPLETED);
    }

    private RegulationDocument findDocument(Long regulationId) {
        return documentRepository.findById(regulationId)
                .orElseThrow(() -> new IllegalArgumentException("규정 문서를 찾을 수 없습니다."));
    }

    @Transactional
    protected void updateStatus(Long regulationId, RegulationIngestionStatus status) {
        documentRepository.findById(regulationId)
                .ifPresent(document -> document.updateIngestionStatus(status));
    }
}
