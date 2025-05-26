package com.thiCK.Nhom16.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/qtv")
public class QuanTriController {

    /**
     * Hiển thị dashboard cho Quản trị viên
     */
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard() {
        // Sẽ render file src/main/resources/templates/dashboard/dashboard.html
        return "dashboard/dashboard";
    }
}