package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.User;
import com.thiCK.Nhom16.repository.UserRepository;
import com.thiCK.Nhom16.service.UserService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;

    public AuthController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Khi click Đăng nhập => redirect về /user/profile?login=true
     * để Thymeleaf bật login_modal
     */
    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/user/profile?login=true";
    }

    /**
     * Xử lý Đăng ký qua form trong login_modal
     */
    @PostMapping("/register")
    public String register(
            @ModelAttribute("user") @Valid User user,
            BindingResult result,
            Model model) {

        // Kiểm tra email trùng
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            result.rejectValue("email", null, "Email đã được sử dụng");
        }

        if (result.hasErrors()) {
            // Nếu lỗi validation, render lại profile.html và bật tab Đăng ký
            model.addAttribute("modalRegister", true);
            return "user/profile";
        }

        try {
            // Gán role mặc định USER khi register
            user.setRole("ROLE_USER");
            userService.register(user);
        } catch (DataIntegrityViolationException ex) {
            // Lỗi ràng buộc unique trên DB
            result.rejectValue("email", null, "Email đã được sử dụng");
            model.addAttribute("modalRegister", true);
            return "user/profile";
        }

        // Đăng ký thành công => chuyển về profile, bật login modal và hiện thông báo registered
        return "redirect:/user/profile?login=true&registered=true";
    }

    /**
     * Hiển thị trang profile (chứa cả login_modal)
     * Áp dụng cho cả / và /user/profile
     */
    @GetMapping({"/", "/user/profile"})
    public String profilePage(
            Principal principal,
            @RequestParam(value = "login",      required = false) Boolean login,
            @RequestParam(value = "logout",     required = false) Boolean logout,
            @RequestParam(value = "registered", required = false) Boolean registered,
            Model model) {

        // Lấy entity User từ DB dựa vào principal.getName()
        User u;
        if (principal != null) {
            u = userRepository.findByUsername(principal.getName())
                    .orElse(new User());
        } else {
            u = new User();
        }
        model.addAttribute("user", u);

        // Flags để Thymeleaf bật tương ứng các modal/login/logout/registered
        if (Boolean.TRUE.equals(login))      model.addAttribute("login", true);
        if (Boolean.TRUE.equals(logout))     model.addAttribute("logout", true);
        if (Boolean.TRUE.equals(registered)) model.addAttribute("registered", true);

        return "user/profile";
    }
}
