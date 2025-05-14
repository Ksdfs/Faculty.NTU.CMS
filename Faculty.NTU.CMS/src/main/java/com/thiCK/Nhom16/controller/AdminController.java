package com.thiCK.Nhom16.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class AdminController {
	@GetMapping
    public String adminPage() {
        return "admin";   // templates/admin.html
    }
}
