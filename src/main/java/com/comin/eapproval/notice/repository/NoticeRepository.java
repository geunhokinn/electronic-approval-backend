package com.comin.eapproval.notice.repository;

import com.comin.eapproval.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 공지사항 데이터 접근 기능을 제공하는 Repository.
 */
public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {

    /**
     * 삭제되지 않은 공지사항을 ID로 조회한다.
     *
     * @param noticeId 공지사항 ID
     * @return 조회된 공지사항
     */
    Optional<Notice> findByIdAndDeletedAtIsNull(Long noticeId);
}
