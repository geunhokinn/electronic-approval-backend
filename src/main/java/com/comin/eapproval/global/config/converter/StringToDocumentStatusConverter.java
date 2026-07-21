package com.comin.eapproval.global.config.converter;

import com.comin.eapproval.document.entity.enums.DocumentStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 요청 문자열을 문서 상태로 변환한다.
 */
@Component
public class StringToDocumentStatusConverter
        implements Converter<String, DocumentStatus> {

    @Override
    public DocumentStatus convert(String source) {
        return DocumentStatus.valueOf(
                source.trim().toUpperCase(Locale.ROOT)
        );
    }
}
