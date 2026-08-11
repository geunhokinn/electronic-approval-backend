package com.comin.eapproval.regulation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 검색과 임베딩을 위해 규정 원문을 나눈 청크 엔티티입니다.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegulationChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "regulation_document_id", nullable = false)
    private RegulationDocument regulationDocument;

    @Column(nullable = false)
    private int chunkIndex;

    @Column(length = 200)
    private String sectionTitle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 100)
    private String vectorId;

    @Builder
    private RegulationChunk(RegulationDocument regulationDocument, int chunkIndex,
                            String sectionTitle, String content) {
        this.regulationDocument = regulationDocument;
        this.chunkIndex = chunkIndex;
        this.sectionTitle = sectionTitle;
        this.content = content;
    }

    /**
     * 규정 청크를 생성합니다.
     *
     * @param document 원본 규정
     * @param chunkIndex 청크 순서
     * @param content 청크 내용
     * @return 생성된 규정 청크
     */
    public static RegulationChunk create(RegulationDocument document, int chunkIndex,
                                          String content) {
        return RegulationChunk.builder()
                .regulationDocument(document)
                .chunkIndex(chunkIndex)
                .content(content.trim())
                .build();
    }

    /**
     * Qdrant Point ID를 저장합니다.
     *
     * @param vectorId Qdrant Point ID
     */
    public void updateVectorId(String vectorId) {
        this.vectorId = vectorId;
    }
}
