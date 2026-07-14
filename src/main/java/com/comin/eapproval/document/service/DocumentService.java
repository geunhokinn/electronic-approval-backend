package com.comin.eapproval.document.service;

import com.comin.eapproval.document.dto.request.DocumentCreateRequest;
import com.comin.eapproval.document.dto.response.DocumentCreateResponse;
import com.comin.eapproval.document.dto.response.DocumentListResponse;
import com.comin.eapproval.document.entity.Document;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.comin.eapproval.document.repository.DocumentRepository;
import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.member.MemberErrorCode;
import com.comin.eapproval.global.response.PageResponse;
import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 문서 관련 비즈니스 로직을 처리하는 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final MemberRepository memberRepository;

    /**
     * 로그인한 사용자의 문서를 임시저장한다.
     *
     * @param memberId 로그인 회원 ID
     * @param request 문서 임시저장 요청 정보
     * @return 문서 임시저장 결과
     */
    @Transactional
    public DocumentCreateResponse createDraft(
            Long memberId,
            DocumentCreateRequest request) {

        Member writer = findMember(memberId);

        Document document = Document.createDraft(
                request.getTitle(),
                request.getContent(),
                writer
        );

        Document savedDocument = documentRepository.save(document);

        return DocumentCreateResponse.of(savedDocument.getId());
    }

    /**
     * 로그인 사용자가 작성한 문서 목록을 조회한다.
     *
     * @param memberId 로그인 회원 ID
     * @param status 문서 상태
     * @param keyword 제목 검색어
     * @param pageable 페이징 정보
     * @return 내 문서 목록
     */
    public PageResponse<DocumentListResponse> getMyDocuments(
            Long memberId,
            DocumentStatus status,
            String keyword,
            Pageable pageable
    ) {
        Page<DocumentListResponse> documents = documentRepository
                .searchMyDocuments(
                        memberId,
                        status,
                        keyword,
                        pageable
                )
                .map(DocumentListResponse::from);

        return PageResponse.from(documents);
    }

    /**
     * 회원 ID로 회원을 조회한다.
     *
     * @param memberId 회원 ID
     * @return 조회된 회원
     */
    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new BusinessException(
                                MemberErrorCode.MEMBER_NOT_FOUND
                        )
                );
    }
}
