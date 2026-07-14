package com.comin.eapproval.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 로그인 화면을 제공하는 컨트롤러.
 */
@Controller
public class LoginController {

    /**
     * 로그인 화면을 반환한다.
     *
     * @return 로그인 화면
     */
    @GetMapping("/login")
    public String login() {
        return "member/login";
    }
}
