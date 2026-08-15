package com.comin.eapproval.notice.service;

import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.member.MemberErrorCode;
import com.comin.eapproval.global.exception.notice.NoticeErrorCode;
import com.comin.eapproval.global.response.PageResponse;
import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.repository.MemberRepository;
import com.comin.eapproval.notice.dto.request.NoticeCreateRequest;
import com.comin.eapproval.notice.dto.request.NoticeUpdateRequest;
import com.comin.eapproval.notice.dto.response.NoticeCreateResponse;
import com.comin.eapproval.notice.dto.response.NoticeDetailResponse;
import com.comin.eapproval.notice.dto.response.NoticeListResponse;
import com.comin.eapproval.notice.entity.Notice;
import com.comin.eapproval.notice.entity.NoticeCategory;
import com.comin.eapproval.notice.entity.NoticeType;
import com.comin.eapproval.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 공지사항 관련 비즈니스 로직을 처리하는 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    /**
     * 제목 검색 조건에 해당하는 공지사항 목록을 조회한다.
     *
     * @param keyword 제목 검색어
     * @param noticeType 공지 유형
     * @param noticeCategory 공지 분류
     * @param pageable 페이징 정보
     * @return 공지사항 목록
     */
    public PageResponse<NoticeListResponse> getNotices(
            String keyword,
            NoticeType noticeType,
            NoticeCategory noticeCategory,
            Pageable pageable
    ) {
        Page<NoticeListResponse> notices = noticeRepository.searchNotices(
                keyword,
                noticeType,
                noticeCategory,
                pageable
        );

        return PageResponse.from(notices);
    }

    /**
     * 공지사항 상세 정보를 조회한다.
     *
     * @param noticeId 공지사항 ID
     * @return 공지사항 상세 정보
     */
    public NoticeDetailResponse getNotice(Long noticeId) {
        return noticeRepository.findNoticeDetailById(noticeId)
                .orElseThrow(() -> new BusinessException(
                        NoticeErrorCode.NOTICE_NOT_FOUND
                ));
    }

    /**
     * 공지사항을 등록한다.
     *
     * @param authorId 작성자 ID
     * @param request 공지사항 등록 요청 정보
     * @return 공지사항 등록 결과
     */
    @Transactional
    public NoticeCreateResponse createNotice(
            Long authorId,
            NoticeCreateRequest request
    ) {
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new BusinessException(
                        MemberErrorCode.MEMBER_NOT_FOUND
                ));

        Notice notice = Notice.create(
                request.getTitle(),
                request.getContent(),
                request.getNoticeType(),
                request.getNoticeCategory(),
                author
        );

        Notice savedNotice = noticeRepository.save(notice);

        return NoticeCreateResponse.of(savedNotice.getId());
    }

    /**
     * 공지사항을 수정한다.
     *
     * @param noticeId 공지사항 ID
     * @param request 공지사항 수정 요청 정보
     */
    @Transactional
    public void updateNotice(
            Long noticeId,
            NoticeUpdateRequest request
    ) {
        Notice notice = findNotice(noticeId);

        notice.update(
                request.getTitle(),
                request.getContent(),
                request.getNoticeType(),
                request.getNoticeCategory()
        );
    }

    /**
     * 공지사항을 삭제 처리한다.
     *
     * @param noticeId 공지사항 ID
     */
    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = findNotice(noticeId);
        notice.delete();
    }

    /**
     * 삭제되지 않은 공지사항을 조회한다.
     *
     * @param noticeId 공지사항 ID
     * @return 조회된 공지사항
     */
    private Notice findNotice(Long noticeId) {
        return noticeRepository.findByIdAndDeletedAtIsNull(noticeId)
                .orElseThrow(() -> new BusinessException(
                        NoticeErrorCode.NOTICE_NOT_FOUND
                ));
    }

}
