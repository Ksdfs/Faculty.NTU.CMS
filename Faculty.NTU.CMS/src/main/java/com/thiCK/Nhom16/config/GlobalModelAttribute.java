package com.thiCK.Nhom16.config;

import com.thiCK.Nhom16.entity.Admin;
import com.thiCK.Nhom16.entity.QuanTriVien;
import com.thiCK.Nhom16.entity.User;
import com.thiCK.Nhom16.entity.Menu;
import com.thiCK.Nhom16.entity.SiteSettings;
import com.thiCK.Nhom16.service.MenuService;
import com.thiCK.Nhom16.service.SiteSettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * Tất cả @ModelAttribute ở đây sẽ tự động được thêm
 * vào Model của mọi controller (toàn cục).
 */
@ControllerAdvice
public class GlobalModelAttribute {

    private final MenuService menuService;
    private final SiteSettingsService settingsService;

    @Autowired
    public GlobalModelAttribute(MenuService menuService,
                                SiteSettingsService settingsService) {
        this.menuService = menuService;
        this.settingsService = settingsService;
    }

    /* -------- Bean dùng cho form đăng nhập -------- */
    @ModelAttribute("loginUser")
    public User loginUser() {
        return new User();             // DTO/Entity cho form login
    }

    /* -------- Bean dùng cho form đăng ký -------- */
    @ModelAttribute("registerUser")
    public User registerUser() {
        return new User();             // Hoặc một RegisterDTO riêng nếu bạn có
    }

    /* -------- Thông tin người dùng hiện tại -------- */
    @ModelAttribute("currentUser")
    public User currentUser(HttpSession session) {
        Object u = session.getAttribute("user");
        return (u instanceof User) ? (User) u : null;
    }

    /* -------- Quyền hạn để ẩn/hiện menu -------- */
    @ModelAttribute("isAdmin")
    public boolean isAdmin(HttpSession session) {
        return session.getAttribute("user") instanceof Admin;
    }

    @ModelAttribute("isQtv")
    public boolean isQtv(HttpSession session) {
        return session.getAttribute("user") instanceof QuanTriVien;
    }

    /* -------- Đường dẫn hiện tại để highlight menu -------- */
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /* -------- Danh sách menu cha cho header động -------- */
    @ModelAttribute("headerMenus")
    public List<Menu> headerMenus() {
        return menuService.getHeaderMenus();
    }

    /* -------- Cấu hình chung của site (logo, tên, social…) -------- */
    @ModelAttribute("settings")
    public SiteSettings settings() {
        return settingsService.getSettings();
    }
}
