package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.User;
import com.thiCK.Nhom16.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String showEditForm(
            @AuthenticationPrincipal User currentUser,
            Model model
    ) {
        model.addAttribute("user", currentUser);
        return "user/edit_profile";
    }

    @PostMapping("/edit")
    public String updateProfile(
            @ModelAttribute("user") @Valid User formUser,
            BindingResult result,
            @AuthenticationPrincipal User currentUser,
            Model model
    ) {
        if (result.hasErrors()) {
            return "user/edit_profile";
        }
        // Cập nhật các trường cho currentUser
        currentUser.setUsername(formUser.getUsername());
        currentUser.setEmail(formUser.getEmail());
        // nếu entity User có thêm các trường khác, đặt ở đây...
        userService.save(currentUser);

        model.addAttribute("success", true);
        return "user/edit_profile";
    }


    // ==== Xem danh sách user (Admin) ====
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public String listUsers(Model model) {
        List<User> all = userService.findAll();
        model.addAttribute("users", all);
        return "admin/user_list";
    }


    // ==== Xóa tài khoản (Admin) ====
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/user/list";
    }


    // ==== Thay đổi mật khẩu ====
    @GetMapping("/change_password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("passwordForm", new PasswordForm());
        return "user/change_password";
    }

    @PostMapping("/change_password")
    public String changePassword(
            @ModelAttribute("passwordForm") @Valid PasswordForm form,
            BindingResult result,
            @AuthenticationPrincipal User currentUser,
            Model model
    ) {
        // Kiểm tra password cũ
        if (!userService.checkIfValidOldPassword(currentUser, form.getOldPassword())) {
            result.rejectValue("oldPassword", null, "Mật khẩu cũ không đúng");
        }
        // Kiểm tra xác nhận mật khẩu mới
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", null, "Mật khẩu mới không khớp");
        }

        if (result.hasErrors()) {
            return "user/change_password";
        }

        userService.changePassword(currentUser, form.getNewPassword());
        model.addAttribute("success", true);
        return "user/change_password";
    }


    // ===== DTO lớp nhỏ cho form đổi mật khẩu =====
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
