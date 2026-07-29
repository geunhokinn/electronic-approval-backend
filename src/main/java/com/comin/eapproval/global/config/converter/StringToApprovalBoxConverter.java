package com.comin.eapproval.global.config.converter;

import com.comin.eapproval.approval.entity.enums.ApprovalBox;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 요청 문자열을 결재 목록 구분으로 변환한다.
 */
@Component
public class StringToApprovalBoxConverter
        implements Converter<String, ApprovalBox> {

    /**
     * 요청 문자열을 대문자 결재 목록 구분으로 변환한다.
     *
     * @param source 요청 문자열
     * @return 변환된 결재 목록 구분
     */
    @Override
    public ApprovalBox convert(String source) {
        return ApprovalBox.valueOf(
                source.trim().toUpperCase(Locale.ROOT)
        );
    }
}
