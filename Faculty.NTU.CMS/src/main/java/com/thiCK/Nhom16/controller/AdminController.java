package com.thiCK.Nhom16.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * Hiển thị dashboard cho Admin
     */
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard() {
        // sẽ render file src/main/resources/templates/dashboard/dashboard.html
        return "dashboard/dashboard";
    }
}