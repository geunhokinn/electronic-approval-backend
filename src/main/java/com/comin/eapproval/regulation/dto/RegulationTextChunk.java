package com.comin.eapproval.regulation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Markdown 규정 문서에서 분할한 검색용 청크를 표현합니다.
 */
@Getter
@AllArgsConstructor
public class RegulationTextChunk {

    /** 규정명입니다. */
    private String regulationName;

    /** 조항 번호입니다. */
    private String articleNumber;

    /** 조항 제목입니다. */
    private String articleTitle;

    /** 항 번호입니다. */
    private String paragraphNumber;

    /** 규정 문서 내 청크 순번입니다. */
    private int chunkIndex;

    /** MariaDB와 Qdrant payload에 저장할 청크 본문입니다. */
    private String content;

    /** Bedrock 임베딩 생성에 사용할 문맥 prefix 포함 텍스트입니다. */
    private String embeddingText;
}
