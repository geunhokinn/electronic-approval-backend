package com.comin.eapproval.document.dto.response;

import com.comin.eapproval.document.entity.enums.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DocumentDetailResponse {

    private Long documentId;

    private String title;

    private String content;

    private DocumentStatus status;

    private Long writerId;

    private String writerName;

    private Long approverId;

    private String approverName;

    private String comment;

    private LocalDateTime requestedAt;

    private LocalDateTime processedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long previousDocumentId;
}
//
//import com.comin.eapproval.document.entity.Document;
//import com.comin.eapproval.document.entity.enums.DocumentStatus;
//import lombok.Getter;
//
//import java.time.LocalDateTime;
//
///**
// * 문서 상세 조회 결과를 제공하는 응답 객체.
// */
//@Getter
//public class DocumentDetailResponse {
//
//    private final Long documentId;
//    private final String title;
//    private final String content;
//    private final DocumentStatus status;
//    private final Long writerId;
//    private final String writerName;
//    private final LocalDateTime createdAt;
//    private final LocalDateTime updatedAt;
//
//    private DocumentDetailResponse(
//            Long documentId,
//            String title,
//            String content,
//            DocumentStatus status,
//            Long writerId,
//            String writerName,
//            LocalDateTime createdAt,
//            LocalDateTime updatedAt
//    ) {
//        this.documentId = documentId;
//        this.title = title;
//        this.content = content;
//        this.status = status;
//        this.writerId = writerId;
//        this.writerName = writerName;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }
//
//    /**
//     * Document 엔티티를 문서 상세 응답으로 변환한다.
//     *
//     * @param document 문서 엔티티
//     * @return 문서 상세 응답
//     */
//    public static DocumentDetailResponse from(Document document) {
//        return new DocumentDetailResponse(
//                document.getId(),
//                document.getTitle(),
//                document.getContent(),
//                document.getStatus(),
//                document.getWriter().getId(),
//                document.getWriter().getName(),
//                document.getCreatedAt(),
//                document.getUpdatedAt()
//        );
//    }
//}
