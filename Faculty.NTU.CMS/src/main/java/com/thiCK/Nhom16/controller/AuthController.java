package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Admin;
import com.thiCK.Nhom16.entity.QuanTriVien;
import com.thiCK.Nhom16.entity.User;
import com.thiCK.Nhom16.repository.UserRepository;
import com.thiCK.Nhom16.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    public AuthController(UserService userService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @ModelAttribute("loginUser")
    public User loginUser() {
        return new User();
    }

    @ModelAttribute("registerUser")
    public User registerUser() {
        return new User();
    }

    /**
     * Hiển thị profile, bao gồm login/register modal
     */
    @GetMapping("/profile")
    public String profilePage(HttpSession session,
                              @RequestParam(value = "login", required = false) Boolean login,
                              @RequestParam(value = "logout", required = false) Boolean logout,
                              @RequestParam(value = "registered", required = false) Boolean registered,
                              Model model) {
        Object obj = session.getAttribute("user");
        User u = (obj instanceof User) ? (User) obj : new User();
        model.addAttribute("user", u);
        if (Boolean.TRUE.equals(login))      model.addAttribute("login", true);
        if (Boolean.TRUE.equals(logout))     model.addAttribute("logout", true);
        if (Boolean.TRUE.equals(registered)) model.addAttribute("registered", true);
        return "user/profile";
    }

    /**
     * Redirect root về profile
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/user/profile";
    }

    /**
     * Xử lý đăng ký
     */
    @PostMapping("/register")
    public String register(@ModelAttribute("registerUser") @Valid User registerUser,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("modalRegister", true);
            return "user/profile";
        }
        String error = userService.registerUser(registerUser);
        if (error != null) {
            result.rejectValue("email", null, error);
            model.addAttribute("modalRegister", true);
            return "user/profile";
        }
        return "redirect:/user/profile?registered=true";
    }

    /**
     * Xử lý đăng nhập
     */
    @PostMapping("/login")
    public String login(@ModelAttribute("loginUser") User loginUser,
                        BindingResult loginResult,
                        HttpSession session,
                        Model model) {
        Object u = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (u == null) {
            model.addAttribute("loginError", "Sai tên đăng nhập hoặc mật khẩu");
            model.addAttribute("login", true);
            return "user/profile";
        }
        session.setAttribute("user", u);

        // Chuyển cả Admin và QuanTriVien về dashboard
        if (u instanceof Admin || u instanceof QuanTriVien) {
            return "redirect:/dashboard";
        }
        // Các user khác vẫn trả về profile
        return "redirect:/user/profile";
    }

    /**
     * Đăng xuất
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
