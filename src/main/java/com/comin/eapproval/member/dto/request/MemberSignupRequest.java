package com.comin.eapproval.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSignupRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    @Pattern(
            regexp = "^[a-z0-9]{3,12}$",
            message = "아이디는 영문 소문자 또는 숫자를 사용하여 3~12자로 입력해주세요."
    )
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9])[!-~]{8,64}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~64자로 입력해주세요."
    )
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    @Pattern(
            regexp = "^[가-힣A-Za-z]{2,50}$",
            message = "이름은 한글 또는 영문으로 2~50자로 입력해주세요."
    )
    private String name;

    @NotBlank(message = "본부를 선택해주세요.")
    private String headquarters;

    @NotBlank(message = "팀을 선택해주세요.")
    private String team;

    @NotBlank(message = "직급을 선택해주세요.")
    private String position;
}
