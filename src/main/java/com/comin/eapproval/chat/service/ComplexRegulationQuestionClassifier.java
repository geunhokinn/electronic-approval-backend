package com.comin.eapproval.chat.service;

import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 규정 질문이 여러 주제를 포함하는 복합 질문인지 판단하는 서비스.
 */
@Service
public class ComplexRegulationQuestionClassifier {

    private static final Set<String> REGULATION_TOPICS = Set.of(
            "출장", "휴가", "근태", "구매", "예산", "경비",
            "법인카드", "정보보안", "개인정보", "준법감시", "내부통제",
            "리스크관리", "고객민원", "이상거래", "금융상품"
    );

    private static final Set<String> COMBINATION_WORDS = Set.of(
            "및", "와", "과", "또한", "그리고", "동시에"
    );

    /**
     * 질문이 둘 이상의 규정 주제를 포함하는지 판단한다.
     *
     * @param question 사용자 질문
     * @return 복합 질문이면 true
     */
    public boolean isComplexQuestion(String question) {
        if (question == null || question.isBlank()) {
            return false;
        }

        String normalizedQuestion = question.trim().toLowerCase();
        long topicCount = REGULATION_TOPICS.stream()
                .map(String::toLowerCase)
                .filter(normalizedQuestion::contains)
                .count();

        return topicCount >= 2
                || COMBINATION_WORDS.stream()
                .map(String::toLowerCase)
                .anyMatch(normalizedQuestion::contains);
    }
}
