package com.comin.eapproval.regulation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 규정 원본 업로드 결과입니다. */
@Getter
@AllArgsConstructor
public class RegulationUploadResponse {

    /** 등록된 규정 ID입니다. */
    private Long regulationId;

    /** 규정 제목입니다. */
    private String title;

    /** 규정 버전입니다. */
    private String version;

    /** 사용자가 업로드한 원본 파일명입니다. */
    private String originalFilename;

    /** S3에 저장된 객체 키입니다. */
    private String objectKey;
}
