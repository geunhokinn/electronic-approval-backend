package com.comin.eapproval.chat.service;

import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 사용자의 질문이 사내 규정 검색 대상인지 판단하는 서비스.
 *
 * 초기 단계에서는 별도 분류 모델을 호출하지 않고, 규정명·조항·기준·절차와
 * 같은 규정 관련 표현을 기준으로 1차 분류한다.
 */
@Service
public class RegulationQuestionClassifier {

    private static final Set<String> REGULATION_KEYWORDS = Set.of(
            "규정",
            "규칙",
            "지침",
            "조항",
            "기준",
            "절차",
            "요건",
            "한도",
            "승인 절차",
            "신청 절차",
            "보존 기간",
            "시행일",
            "출장관리",
            "휴가관리",
            "근태관리",
            "구매관리",
            "예산관리",
            "경비처리",
            "법인카드",
            "정보보안",
            "교육훈련",
            "문서관리",
            "기안관리",
            "결재관리",
            "준법감시",
            "내부통제",
            "리스크관리",
            "고객민원",
            "이상거래",
            "자금세탁방지",
            "개인정보보호",
            "금융상품",
            "전결",
            "대결",
            "감사",
            "금융소비자보호"
    );

    /**
     * 질문이 규정 검색 대상인지 판단한다.
     *
     * @param question 사용자 질문
     * @return 규정 관련 표현이 포함되어 있으면 true
     */
    public boolean isRegulationQuestion(String question) {
        if (question == null || question.isBlank()) {
            return false;
        }

        String normalizedQuestion = question.trim().toLowerCase();
        return REGULATION_KEYWORDS.stream()
                .map(String::toLowerCase)
                .anyMatch(normalizedQuestion::contains);
    }
}
