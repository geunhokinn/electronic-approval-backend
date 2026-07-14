package com.comin.eapproval.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 애플리케이션 메인 화면을 제공하는 컨트롤러.
 */
@Controller
public class HomeController {

    /**
     * 메인 화면을 반환한다.
     *
     * @return 메인 화면
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
