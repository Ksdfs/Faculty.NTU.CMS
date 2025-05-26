package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.User;
import com.thiCK.Nhom16.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ==== Chỉnh sửa thông tin cá nhân ====
    @GetMapping("/edit")
    public String showEditForm(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/user/profile?login=true";
        }
        model.addAttribute("user", currentUser);
        return "user/edit_profile";
    }

    @PostMapping("/edit")
    public String updateProfile(
            @ModelAttribute("user") @Valid User formUser,
            BindingResult result,
            HttpSession session,
            Model model
    ) {
        if (result.hasErrors()) {
            return "user/edit_profile";
        }
        User currentUser = (User) session.getAttribute("user");
        currentUser.setUsername(formUser.getUsername());
        currentUser.setEmail(formUser.getEmail());
        userService.save(currentUser);
        session.setAttribute("user", currentUser);
        model.addAttribute("success", true);
        return "user/edit_profile";
    }

    // ==== Xem danh sách user (Admin) ====
    @GetMapping("/list")
    public String listUsers(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return "redirect:/user/profile";
        }
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/user_list";
    }

    // ==== Xóa tài khoản (Admin) ====
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return "redirect:/user/profile";
        }
        userService.deleteUser(id);
        return "redirect:/user/list";
    }

    // ==== Thay đổi mật khẩu ====
    @GetMapping("/change_password")
    public String showChangePasswordForm(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/user/profile?login=true";
        }
        model.addAttribute("passwordForm", new PasswordForm());
        return "user/change_password";
    }

    @PostMapping("/change_password")
    public String changePassword(
            @ModelAttribute("passwordForm") @Valid PasswordForm form,
            BindingResult result,
            HttpSession session,
            Model model
    ) {
        User currentUser = (User) session.getAttribute("user");
        if (!userService.checkOldPassword(currentUser, form.getOldPassword())) {
            result.rejectValue("oldPassword", null, "Mật khẩu cũ không đúng");
        }
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", null, "Mật khẩu mới không khớp");
        }

        if (result.hasErrors()) {
            return "user/change_password";
        }

        userService.changePassword(currentUser, form.getNewPassword());
        session.setAttribute("user", currentUser);
        model.addAttribute("success", true);
        return "user/change_password";
    }

    // ===== DTO cho form đổi mật khẩu =====
    public static class PasswordForm {
        @NotBlank
        private String oldPassword;
        @NotBlank
        private String newPassword;
        @NotBlank
        private String confirmPassword;

        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    }
}
