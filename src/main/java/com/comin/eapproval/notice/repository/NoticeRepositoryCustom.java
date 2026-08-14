package com.comin.eapproval.notice.repository;

import com.comin.eapproval.notice.dto.response.NoticeDetailResponse;
import com.comin.eapproval.notice.dto.response.NoticeListResponse;
import com.comin.eapproval.notice.entity.NoticeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * 공지사항 QueryDSL 조회 기능을 제공하는 커스텀 Repository.
 */
public interface NoticeRepositoryCustom {

    /**
     * 제목 검색 조건에 해당하는 공지사항 목록을 조회한다.
     *
     * @param keyword 제목 검색어
     * @param noticeType 공지사항 유형
     * @param pageable 페이징 정보
     * @return 공지사항 목록
     */
    Page<NoticeListResponse> searchNotices(
            String keyword,
            NoticeType noticeType,
            Pageable pageable
    );

    /**
     * 공지사항 ID로 삭제되지 않은 공지사항 상세 정보를 조회한다.
     *
     * @param noticeId 공지사항 ID
     * @return 공지사항 상세 정보
     */
    Optional<NoticeDetailResponse> findNoticeDetailById(Long noticeId);
}
