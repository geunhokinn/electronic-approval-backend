package com.comin.eapproval.regulation.controller;

import com.comin.eapproval.global.response.ApiResponse;
import com.comin.eapproval.regulation.dto.request.RegulationUploadRequest;
import com.comin.eapproval.regulation.dto.response.RegulationUploadResponse;
import com.comin.eapproval.regulation.service.RegulationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** 관리자 규정 원본 업로드 API를 제공합니다. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/regulations")
public class AdminRegulationController {

    private final RegulationService regulationService;

    /** 규정 메타데이터와 원본 파일을 등록합니다. */
    /**
     * 관리자 규정 원본 파일 업로드 API를 제공합니다.
     *
     * @param request 규정 제목·분류·버전
     * @param file 규정 원본 파일
     * @return 업로드 결과
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RegulationUploadResponse>> uploadRegulation(
            @Valid @RequestPart("request") RegulationUploadRequest request,
            @RequestPart("file") MultipartFile file
    ) {
        RegulationUploadResponse response = regulationService.upload(request, file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("규정 파일이 업로드되었습니다.", response));
    }
}
