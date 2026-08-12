package com.comin.eapproval.chat.dto.response;

import com.comin.eapproval.document.dto.response.DocumentToolSearchResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 문서 검색 결과를 후속 상세 Tool 호출로 연결하기 위한 응답입니다.
 */
@Getter
@AllArgsConstructor
public class DocumentToolRouteResponse {

    /** 문서 조회 라우팅 유형입니다. */
    private DocumentToolRouteType type;

    /** 후속 상세 조회에 사용할 문서 ID입니다. */
    private Long documentId;

    /** 사용자 선택이 필요한 검색 결과 목록입니다. */
    private List<DocumentToolSearchResponse> candidates;

    /** 사용자에게 전달할 안내 메시지입니다. */
    private String message;

    /**
     * 특정 문서 상세 조회 라우팅 결과를 생성합니다.
     *
     * @param documentId 문서 식별자
     * @return 상세 조회 결과
     */
    public static DocumentToolRouteResponse detail(Long documentId) {
        return new DocumentToolRouteResponse(
                DocumentToolRouteType.DETAIL,
                documentId,
                List.of(),
                null
        );
    }

    /**
     * 추론된 문서에 대한 사용자 확인 요청 결과를 생성합니다.
     *
     * @param candidate 확인할 문서 후보
     * @return 확인 요청 결과
     */
    public static DocumentToolRouteResponse confirm(
            DocumentToolSearchResponse candidate
    ) {
        return new DocumentToolRouteResponse(
                DocumentToolRouteType.CONFIRM_REQUIRED,
                candidate.getDocumentId(),
                List.of(candidate),
                "다음 문서를 조회할까요? 문서 번호나 제목으로 확인해주세요."
        );
    }

    /**
     * 선택 요청 라우팅 결과를 생성합니다.
     *
     * @param candidates 검색 후보 목록
     * @return 선택 요청 결과
     */
    public static DocumentToolRouteResponse select(
            List<DocumentToolSearchResponse> candidates
    ) {
        return new DocumentToolRouteResponse(
                DocumentToolRouteType.SELECT_REQUIRED,
                null,
                candidates,
                "조건에 맞는 문서가 여러 건 있습니다. 문서 번호를 선택해주세요."
        );
    }

    /** 검색 결과 없음 라우팅 결과를 생성합니다. */
    public static DocumentToolRouteResponse notFound() {
        return new DocumentToolRouteResponse(
                DocumentToolRouteType.NOT_FOUND,
                null,
                List.of(),
                "조건에 맞는 문서를 찾지 못했습니다."
        );
    }
}
