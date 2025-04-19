package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Notification;
import com.thiCK.Nhom16.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepo;

    // 📄 Danh sách + lọc theo status
    @GetMapping("/notification/all")
    public String listNotifications(@RequestParam(name = "status", required = false) String status,
                                    Model model) {
        List<Notification> notifications;
        if (status == null || status.isEmpty()) {
            notifications = notificationRepo.findAll();
        } else {
            notifications = notificationRepo.findByStatus(status);
        }

        // Tách riêng Published để dùng cho cột trái
        List<Notification> published = notificationRepo.findByStatus("Published");

        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationsPublished", published);
        model.addAttribute("status", status);
        return "notification/notification_list";
    }


    // 🆕 Hiển thị form tạo mới
    @GetMapping("/notification/new")
    public String showCreateForm(Model model) {
        model.addAttribute("notification", new Notification());
        return "notification/notification_form";
    }

    // 💾 Lưu hoặc cập nhật
    @PostMapping("/notification/save")
    public String saveNotification(@ModelAttribute("notification") Notification notification) {
        notificationRepo.save(notification);
        return "redirect:/notification/all";
    }

    // 🔍 Xem chi tiết
    @GetMapping("/notification/view/{id}")
    public String viewNotification(@PathVariable("id") Long id, Model model) {
        Notification noti = notificationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
        model.addAttribute("notification", noti);
        return "notification/notification_view";
    }

    // ❌ Xoá
    @GetMapping("/notification/delete/{id}")
    public String deleteNotification(@PathVariable("id") Long id) {
        notificationRepo.deleteById(id);
        return "redirect:/notification/all";
    }
}
