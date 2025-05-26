package com.thiCK.Nhom16.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /** Khi vào “/” thì redirect về profile (modal login/register) */
    @GetMapping("/")
    public String root() {
        return "redirect:/user/profile";
    }

    /** Nếu ai vẫn cố gõ “/login” thì cũng redirect về modal login */
    @GetMapping("/login")
    public String login() {
        return "redirect:/user/profile?login=true";
    }
}
