package com.comin.eapproval.global.constant;

import java.util.List;
import java.util.Map;

/**
 * 조직 기준 정보를 정의하는 상수 클래스.
 */
public final class OrganizationConstants {

    private OrganizationConstants() {
    }

    /**
     * 허용되는 직급.
     */
    public static final List<String> POSITIONS = List.of(
            "사원",
            "대리",
            "과장",
            "차장",
            "부장"
    );

    /**
     * 본부별 팀 정보.
     */
    public static final Map<String, List<String>> HEADQUARTERS = Map.of(
            "개발본부", List.of(
                    "개발1팀",
                    "개발2팀"
            ),
            "경영관리본부", List.of(
                    "인사팀",
                    "총무팀",
                    "재무팀"
            ),
            "사업본부", List.of(
                    "영업팀",
                    "마케팅팀",
                    "기획팀"
            ),
            "디자인본부", List.of(
                    "디자인1팀",
                    "디자인2팀"
            )
    );
}