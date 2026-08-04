package com.comin.eapproval.document.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 문서 임시저장 요청 정보를 전달하는 객체.
 */
@Getter
@NoArgsConstructor
public class DocumentCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 1000, message = "내용은 1000자 이하로 입력해야 합니다.")
    private String content;

    private Long previousDocumentId;

    @Size(max = 3, message = "첨부파일은 최대 3개까지 선택할 수 있습니다.")
    private List<Long> attachmentIds;
}
