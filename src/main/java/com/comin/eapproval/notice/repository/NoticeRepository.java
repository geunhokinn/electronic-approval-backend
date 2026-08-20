package com.comin.eapproval.notice.repository;

import com.comin.eapproval.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 공지사항 데이터 접근 기능을 제공하는 Repository.
 */
public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {

    /**
     * 작성자와 제목이 같은 삭제되지 않은 공지사항이 존재하는지 확인한다.
     *
     * @param authorId 작성자 ID
     * @param title 공지사항 제목
     * @return 공지사항 존재 여부
     */
    boolean existsByAuthorIdAndTitleAndDeletedAtIsNull(Long authorId, String title);

    /**
     * 삭제되지 않은 공지사항을 ID로 조회한다.
     *
     * @param noticeId 공지사항 ID
     * @return 조회된 공지사항
     */
    Optional<Notice> findByIdAndDeletedAtIsNull(Long noticeId);
}
