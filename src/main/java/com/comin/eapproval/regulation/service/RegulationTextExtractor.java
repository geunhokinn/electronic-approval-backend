package com.comin.eapproval.regulation.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 규정 원본 파일에서 검색에 사용할 텍스트를 추출하는 서비스입니다.
 */
@Service
public class RegulationTextExtractor {

    private final Tika tika = new Tika();

    /**
     * 규정 원본 파일의 텍스트를 추출합니다.
     *
     * @param content 파일 바이트 데이터
     * @param filename 원본 파일명
     * @return 추출된 텍스트
     * @throws IOException 파일 분석 중 오류가 발생한 경우
     */
    public String extract(byte[] content, String filename) throws IOException {
        try {
            String text = tika.parseToString(new java.io.ByteArrayInputStream(content));
            return normalize(text);
        } catch (Exception exception) {
            throw new IOException("규정 파일 텍스트 추출에 실패했습니다.", exception);
        }
    }

    /** 추출 결과의 불필요한 공백과 빈 줄을 정리합니다. */
    private String normalize(String text) {
        return text == null
                ? ""
                : text.replace("\r\n", "\n")
                        .replaceAll("[ \\t]+", " ")
                        .replaceAll("\\n{3,}", "\\n\\n")
                        .trim();
    }
}
