package com.comin.eapproval.regulation.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Markdown 규정 원본 파일에서 검색에 사용할 텍스트를 추출하는 서비스입니다.
 */
@Service
public class RegulationTextExtractor {

    /**
     * UTF-8로 저장된 Markdown 원문을 읽고 기본 정제합니다.
     *
     * @param content Markdown 파일 바이트 데이터
     * @param filename 원본 파일명
     * @return 정제된 Markdown 텍스트
     * @throws IOException 파일 데이터가 없는 경우
     */
    public String extract(byte[] content, String filename) throws IOException {
        if (content == null) {
            throw new IOException("Markdown 파일 데이터가 없습니다.");
        }

        return normalize(new String(content, StandardCharsets.UTF_8));
    }

    /**
     * Markdown 구조를 유지하면서 불필요한 공백과 빈 줄을 정리합니다.
     *
     * @param text 추출된 Markdown 텍스트
     * @return 정제된 텍스트
     */
    private String normalize(String text) {
        return text.replace("\uFEFF", "")
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replaceAll("[ \\t]+", " ")
                .replaceAll("\\n{3,}", "\\n\\n")
                .trim();
    }
}
