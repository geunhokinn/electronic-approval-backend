package com.comin.eapproval.document.repository;

import com.comin.eapproval.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 문서 데이터 접근 기능을 제공하는 Repository.
 */
public interface DocumentRepository extends JpaRepository<Document, Long>, DocumentRepositoryCustom {

    /**
     * 문서 ID로 삭제되지 않은 문서를 조회한다.
     *
     * @param documentId 문서 ID
     * @return 조회된 문서
     */
    Optional<Document> findByIdAndDeletedAtIsNull(Long documentId);

    /**
     * 특정 이전 문서를 참조하는 삭제되지 않은 문서가 존재하는지 확인한다.
     *
     * @param previousDocumentId 이전 문서 ID
     * @return 참조 문서 존재 여부
     */
    boolean existsByPreviousDocumentIdAndDeletedAtIsNull(
            Long previousDocumentId
    );
}
