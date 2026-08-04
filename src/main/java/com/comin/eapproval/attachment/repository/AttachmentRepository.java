package com.comin.eapproval.attachment.repository;

import com.comin.eapproval.attachment.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 첨부파일 데이터 접근 기능을 제공하는 Repository입니다.
 */
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    /**
     * 첨부파일 ID로 삭제되지 않은 첨부파일을 조회합니다.
     *
     * @param attachmentId 첨부파일 ID
     * @return 조회한 첨부파일
     */
    Optional<Attachment> findByIdAndDeletedAtIsNull(Long attachmentId);

    /**
     * 문서에 포함된 삭제되지 않은 첨부파일을 업로드 순서로 조회합니다.
     *
     * @param documentId 문서 ID
     * @return 문서 첨부파일 목록
     */
    List<Attachment> findAllByDocumentIdAndDeletedAtIsNullOrderByCreatedAtAsc(
            Long documentId
    );

    /**
     * 문서에 포함된 삭제되지 않은 첨부파일 개수를 조회합니다.
     *
     * @param documentId 문서 ID
     * @return 문서 첨부파일 개수
     */
    long countByDocumentIdAndDeletedAtIsNull(Long documentId);
}
