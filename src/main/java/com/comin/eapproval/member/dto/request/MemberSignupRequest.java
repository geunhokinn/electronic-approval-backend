package com.comin.eapproval.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSignupRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(max = 100, message = "아이디는 100자 이하로 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50, message = "이름은 50자 이하로 입력해주세요.")
    private String name;

    @NotBlank(message = "본부는 필수입니다.")
    private String headquarters;

    @NotBlank(message = "팀은 필수입니다.")
    private String team;

    @NotBlank(message = "직급은 필수입니다.")
    private String position;
}
