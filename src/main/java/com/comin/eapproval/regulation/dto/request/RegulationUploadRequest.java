package com.comin.eapproval.regulation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 관리자 규정 업로드에 필요한 메타데이터입니다. */
@Getter
@NoArgsConstructor
public class RegulationUploadRequest {

    /** 규정 제목입니다. */
    @NotBlank(message = "규정 제목은 필수입니다.")
    @Size(max = 200, message = "규정 제목은 200자 이하여야 합니다.")
    private String title;

    /** 규정 분류입니다. */
    @NotBlank(message = "규정 분류는 필수입니다.")
    @Size(max = 100, message = "규정 분류는 100자 이하여야 합니다.")
    private String category;

    /** v1, v2 형식의 규정 버전입니다. */
    @NotBlank(message = "규정 버전은 필수입니다.")
    @Pattern(regexp = "v[0-9]+", message = "규정 버전은 v1, v2 형식이어야 합니다.")
    private String version;
}
