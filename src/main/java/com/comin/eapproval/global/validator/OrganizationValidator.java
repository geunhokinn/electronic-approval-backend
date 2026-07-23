package com.comin.eapproval.global.validator;

import com.comin.eapproval.global.constant.OrganizationConstants;
import com.comin.eapproval.global.exception.BusinessException;
import com.comin.eapproval.global.exception.member.MemberErrorCode;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 회원의 조직 정보를 검증하는 클래스.
 */
@Component
public class OrganizationValidator {

    /**
     * 본부, 팀, 직급이 조직 기준에 맞는지 검증한다.
     *
     * @param headquarters 본부
     * @param team 팀
     * @param position 직급
     */
    public void validate(
            String headquarters,
            String team,
            String position
    ) {
        validateHeadquartersAndTeam(headquarters, team);
        validatePosition(position);
    }

    /**
     * 본부가 존재하고 해당 팀이 본부에 소속되는지 검증한다.
     */
    private void validateHeadquartersAndTeam(
            String headquarters,
            String team
    ) {
        List<String> teams =
                OrganizationConstants.HEADQUARTERS.get(headquarters);

        if (teams == null) {
            throw new BusinessException(
                    MemberErrorCode.INVALID_HEADQUARTERS
            );
        }

        if (!teams.contains(team)) {
            throw new BusinessException(
                    MemberErrorCode.INVALID_TEAM
            );
        }
    }

    /**
     * 허용된 직급인지 검증한다.
     */
    private void validatePosition(String position) {
        if (!OrganizationConstants.POSITIONS.contains(position)) {
            throw new BusinessException(
                    MemberErrorCode.INVALID_POSITION
            );
        }
    }
}