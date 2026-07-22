package com.comin.eapproval.document.service;

import com.comin.eapproval.approval.entity.Approval;
import com.comin.eapproval.approval.repository.ApprovalRepository;
import com.comin.eapproval.auditLog.annotation.Audit;
import com.comin.eapproval.auditLog.context.AuditContext;
import com.comin.eapproval.auditLog.context.AuditContextHolder;
import com.comin.eapproval.auditLog.entity.enums.AuditLogAction;
import com.comin.eapproval.document.dto.request.DocumentApprovalRequest;
import com.comin.eapproval.document.dto.request.DocumentCreateRequest;
import com.comin.eapproval.document.dto.request.DocumentUpdateRequest;
import com.comin.eapproval.document.dto.response.DocumentCreateResponse;
import com.comin.eapproval.document.dto.response.DocumentDetailResponse;
import com.comin.eapproval.document.dto.response.DocumentListResponse;
import com.comin.eapproval.document.dto.response.DocumentResubmitResponse;
import com.comin.eapproval.document.entity.Document;
import com.comin.eapproval.document.entity.enums.DocumentStatus;
import com.comin.eapproval.document.repository.DocumentRepository;
import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.document.DocumentErrorCode;
import com.comin.eapproval.global.exception.member.MemberErrorCode;
import com.comin.eapproval.global.response.PageResponse;
import com.comin.eapproval.member.entity.Member;
import com.comin.eapproval.member.entity.enums.MemberRole;
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
    private final ApprovalRepository approvalRepository;

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

        Document previousDocument = null;

        if (request.getPreviousDocumentId() != null) {
            previousDocument = findDocument(request.getPreviousDocumentId());

            validateWriter(previousDocument, memberId);
            validateResubmittable(previousDocument);
            validateNotAlreadyResubmitted(previousDocument);
        }

        Document document = Document.createDraft(
                request.getTitle(),
                request.getContent(),
                writer,
                previousDocument
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
                );

        return PageResponse.from(documents);
    }

    /**
     * 문서 상세 정보를 조회한다.
     *
     * 임시저장 문서는 작성자와 관리자만 조회할 수 있으며,
     * 결재 요청 이후의 문서는 작성자, 결재자, 관리자만 조회할 수 있다.
     *
     * @param documentId 문서 ID
     * @param memberId 로그인 회원 ID
     * @param role 로그인 회원 권한
     * @return 문서 상세 정보
     */
    public DocumentDetailResponse getDocumentDetail(
            Long documentId,
            Long memberId,
            MemberRole role
    ) {
        Document document = findDocument(documentId);

        validateReadPermission(
                document,
                memberId,
                role
        );

        return findDocumentDetailResponse(documentId);
//        return DocumentDetailResponse.from(document);
    }

    /**
     * 재상신할 문서 정보를 조회한다.
     *
     * @param documentId 문서 ID
     * @param memberId 로그인 회원 ID
     * @return 재상신 문서 정보
     */
    public DocumentResubmitResponse getResubmitDocument(
            Long documentId,
            Long memberId
    ) {
        Document document = findDocument(documentId);

        validateWriter(document, memberId);
        validateResubmittable(document);
        validateNotAlreadyResubmitted(document);

        return findResubmitDocumentResponse(documentId);
    }

    /**
     * 로그인 사용자가 작성한 문서를 수정한다.
     *
     * 작성자 본인의 임시저장 문서만 수정할 수 있다.
     *
     * @param documentId 문서 ID
     * @param memberId 로그인 회원 ID
     * @param request 문서 수정 요청 정보
     */
    @Transactional
    public void updateDocument(
            Long documentId,
            Long memberId,
            DocumentUpdateRequest request
    ) {
        Document document = findDocument(documentId);

        validateWriter(document, memberId);
        validateEditable(document);

        document.update(
                request.getTitle(),
                request.getContent()
        );
    }

    /**
     * 로그인 사용자가 작성한 문서를 삭제한다.
     *
     * 작성자 본인의 임시저장 문서만 삭제할 수 있다.
     *
     * @param documentId 문서 ID
     * @param memberId 로그인 회원 ID
     */
    @Transactional
    public void deleteDocument(
            Long documentId,
            Long memberId
    ) {
        Document document = findDocument(documentId);

        validateDeletePermission(document, memberId);
        validateDeletable(document);

        document.delete();
    }

    /**
     * 문서의 결재를 요청한다.
     *
     * @param memberId 요청 회원 ID
     * @param documentId 문서 ID
     * @param request 결재 요청 정보
     */
    @Audit(action = AuditLogAction.REQUEST)
    @Transactional
    public void requestApproval(
            Long memberId,
            Long documentId,
            DocumentApprovalRequest request
    ) {
        Document document = findDocument(documentId);

        AuditContextHolder.set(
                AuditContext.of(
                        memberId,
                        document.getId()
                )
        );

        Member approver = findMember(request.getApproverId());

        validateWriter(document, memberId);
        validateRequestable(document);
        validateSelfApproval(memberId, approver.getId());
        validateApproverRole(approver);

        Approval approval = Approval.create(document, approver);

        document.changeToPending();
        approvalRepository.save(approval);
    }

    /**
     *  private method.
     */

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

    /**
     * 문서가 재상신 가능한 상태인지 검증한다.
     *
     * @param document 문서
     */
    private void validateResubmittable(Document document) {
        if (document.getStatus() != DocumentStatus.REJECTED) {
            throw new BusinessException(
                    DocumentErrorCode.DOCUMENT_NOT_RESUBMITTABLE
            );
        }
    }

    /**
     * 이미 재상신된 문서인지 검증한다.
     *
     * @param document 이전 반려 문서
     */
    private void validateNotAlreadyResubmitted(Document document) {
        boolean alreadyResubmitted =
                documentRepository
                        .existsByPreviousDocumentIdAndDeletedAtIsNull(
                                document.getId()
                        );

        if (alreadyResubmitted) {
            throw new BusinessException(
                    DocumentErrorCode.DOCUMENT_ALREADY_RESUBMITTED
            );
        }
    }

    /**
     * 문서 ID로 삭제되지 않은 문서를 조회한다.
     *
     * @param documentId 문서 ID
     * @return 조회된 문서
     */
    private Document findDocument(Long documentId) {
        return documentRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() ->
                        new BusinessException(
                                DocumentErrorCode.DOCUMENT_NOT_FOUND
                        )
                );
    }

    /**
     * 문서 ID로 삭제되지 않은 문서의 상세 정보를 조회한다.
     *
     * @param documentId 문서 ID
     * @return 조회된 문서 상세 정보
     */
    private DocumentDetailResponse findDocumentDetailResponse(Long documentId) {
        return documentRepository.findDocumentDetailById(documentId)
                .orElseThrow(() ->
                        new BusinessException(
                                DocumentErrorCode.DOCUMENT_NOT_FOUND
                        )
                );
    }

    /**
     * 문서 ID로 재상신 화면에 필요한 문서 정보를 조회한다.
     *
     * @param documentId 문서 ID
     * @return 재상신 문서 정보
     */
    private DocumentResubmitResponse findResubmitDocumentResponse(
            Long documentId
    ) {
        return documentRepository.findResubmitDocument(documentId)
                .orElseThrow(() ->
                        new BusinessException(
                                DocumentErrorCode.DOCUMENT_NOT_FOUND
                        )
                );
    }

    /**
     * 문서 상세 조회 권한을 검증한다.
     *
     * 임시저장 문서는 작성자와 관리자만 조회할 수 있으며,
     * 결재 요청 이후의 문서는 작성자, 결재자, 관리자만 조회할 수 있다.
     *
     * @param document 문서
     * @param memberId 로그인 회원 ID
     * @param role 로그인 회원 권한
     */
    private void validateReadPermission(
            Document document,
            Long memberId,
            MemberRole role
    ) {
        boolean isWriter = document.getWriter()
                .getId()
                .equals(memberId);

        boolean isAdmin = role == MemberRole.ROLE_ADMIN;

        if (document.getStatus() == DocumentStatus.DRAFT) {
            if (!isWriter && !isAdmin) {
                throw new BusinessException(
                        DocumentErrorCode.DOCUMENT_ACCESS_DENIED
                );
            }
            return;
        }

        boolean isApprover =
                approvalRepository.existsByDocumentIdAndApproverId(
                        document.getId(),
                        memberId
                );

        if (!isWriter && !isApprover && !isAdmin) {
            throw new BusinessException(
                    DocumentErrorCode.DOCUMENT_ACCESS_DENIED
            );
        }
    }

    /**
     * 로그인 사용자가 문서 작성자인지 검증한다.
     *
     * @param document 문서
     * @param memberId 로그인 회원 ID
     */
    private void validateWriter(
            Document document,
            Long memberId
    ) {
        if (!document.getWriter().getId().equals(memberId)) {
            throw new BusinessException(
                    DocumentErrorCode.DOCUMENT_UPDATE_DENIED
            );
        }
    }

    /**
     * 문서가 수정 가능한 상태인지 검증한다.
     *
     * @param document 문서
     */
    private void validateEditable(Document document) {
        if (document.getStatus() != DocumentStatus.DRAFT) {
            throw new BusinessException(
                    DocumentErrorCode.DOCUMENT_NOT_EDITABLE
            );
        }
    }

    /**
     * 문서가 삭제 가능한 상태인지 검증한다.
     *
     * @param document 문서
     */
    private void validateDeletable(Document document) {
        if (document.getStatus() != DocumentStatus.DRAFT) {
            throw new BusinessException(
                    DocumentErrorCode.DOCUMENT_NOT_DELETABLE
            );
        }
    }

    /**
     * 로그인 사용자가 문서 작성자인지 검증한다.
     *
     * @param document 문서
     * @param memberId 로그인 회원 ID
     */
    private void validateDeletePermission(
            Document document,
            Long memberId
    ) {
        if (!document.getWriter().getId().equals(memberId)) {
            throw new BusinessException(
                    DocumentErrorCode.DOCUMENT_DELETE_DENIED
            );
        }
    }

    /**
     * 문서가 결재 요청 가능한 상태인지 검증한다.
     *
     * @param document 문서
     */
    private void validateRequestable(Document document) {
        if (document.getStatus() != DocumentStatus.DRAFT) {
            throw new BusinessException(
                    DocumentErrorCode.DOCUMENT_NOT_REQUESTABLE
            );
        }
    }

    /**
     * 작성자가 본인을 결재자로 지정했는지 검증한다.
     *
     * @param writerId 작성자 ID
     * @param approverId 결재자 ID
     */
    private void validateSelfApproval(
            Long writerId,
            Long approverId
    ) {
        if (writerId.equals(approverId)) {
            throw new BusinessException(
                    DocumentErrorCode.SELF_APPROVAL_NOT_ALLOWED
            );
        }
    }

    /**
     * 결재자가 결재 권한을 가지고 있는지 검증한다.
     *
     * @param approver 결재자
     */
    private void validateApproverRole(Member approver) {
        if (approver.getRole() != MemberRole.ROLE_APPROVER) {
            throw new BusinessException(
                    DocumentErrorCode.INVALID_APPROVER
            );
        }
    }
}
