package com.comin.eapproval.document.repository;

import com.comin.eapproval.document.dto.response.DocumentDetailResponse;
import com.comin.eapproval.document.dto.response.DocumentListResponse;
import com.comin.eapproval.document.dto.response.DocumentResubmitResponse;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * QueryDSL을 이용한 문서 조회 기능을 정의하는 Repository.
 */
public interface DocumentRepositoryCustom {

    /**
     * 로그인 사용자가 작성한 문서 목록을 조회한다.
     *
     * @param writerId 작성자 회원 ID
     * @param status 문서 상태
     * @param keyword 제목 검색어
     * @param pageable 페이징 정보
     * @return 조건에 맞는 문서 목록
     */
    Page<DocumentListResponse> searchMyDocuments(
            Long writerId,
            DocumentStatus status,
            String keyword,
            Pageable pageable
    );

//    /**
//     * 문서 ID로 삭제되지 않은 문서를 조회한다.
//     *
//     * @param documentId 문서 ID
//     * @return 조회된 문서
//     */
//    Optional<Document> findDetailById(Long documentId);

    /**
     * 문서 ID로 삭제되지 않은 문서의 상세 정보를 조회한다.
     *
     * 작성자와 결재 정보를 함께 조회한다.
     *
     * @param documentId 문서 ID
     * @return 문서 상세 정보
     */
    Optional<DocumentDetailResponse> findDocumentDetailById(
            Long documentId
    );

    /**
     * 문서 ID로 재상신 화면에 필요한 문서 정보를 조회한다.
     *
     * 기존 문서의 제목, 내용, 결재자 정보를 함께 조회한다.
     *
     * @param documentId 문서 ID
     * @return 재상신 문서 정보
     */
    Optional<DocumentResubmitResponse> findResubmitDocument(Long documentId);
}
