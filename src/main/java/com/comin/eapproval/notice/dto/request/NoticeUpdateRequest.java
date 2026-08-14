package com.comin.eapproval.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.comin.eapproval.notice.entity.NoticeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공지사항 수정 요청 정보를 전달하는 객체.
 */
@Getter
@NoArgsConstructor
public class NoticeUpdateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 1000, message = "내용은 1000자 이하로 입력해야 합니다.")
    private String content;

    @NotNull(message = "공지 유형은 필수입니다.")
    private NoticeType noticeType;
}
