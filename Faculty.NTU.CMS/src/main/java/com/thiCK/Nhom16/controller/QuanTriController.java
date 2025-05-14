package com.thiCK.Nhom16.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class QuanTriController {
    @GetMapping
    public String quanTriPage() {
        return "quantri"; // templates/quantri.html
    }
}
