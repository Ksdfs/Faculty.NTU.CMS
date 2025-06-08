package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Notification;
import com.thiCK.Nhom16.repository.NotificationRepository;
import com.thiCK.Nhom16.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationRepository notificationRepo;
    private final ActivityService activityService;
    @Autowired private NotificationRepository repo;

    @Autowired
    public NotificationController(NotificationRepository notificationRepo,
                                  ActivityService activityService) {
        this.notificationRepo = notificationRepo;
        this.activityService  = activityService;
    }

    // 📄 Danh sách + lọc theo status
    @GetMapping("/all")
    public String listNotifications(@RequestParam(name = "status", required = false) String status,
                                    Model model) {
        List<Notification> notifications;
        if (status == null || status.isEmpty()) {
            notifications = notificationRepo.findAll();
        } else {
            notifications = notificationRepo.findByStatus(status);
        }
        List<Notification> published = notificationRepo.findByStatus("Published");

        model.addAttribute("notifications",          notifications);
        model.addAttribute("notificationsPublished", published);
        model.addAttribute("status",                 status);
        return "notification/notification_list";
    }

    // 🆕 Form tạo mới
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("notification", new Notification());
        return "notification/notification_form";
    }

    // 💾 Lưu hoặc cập nhật
    @PostMapping("/save")
    public String saveNotification(@ModelAttribute("notification") Notification notification) {
        boolean isNew = (notification.getId() == null);
        Notification saved = notificationRepo.save(notification);

        if (isNew) {
            activityService.log(
                "Created announcement '" + saved.getTitle() + "'",
                "/notification/view/" + saved.getId(),
                "calendar-event"           // icon tạo mới
            );
        } else {
            activityService.log(
                "Updated announcement '" + saved.getTitle() + "'",
                "/notification/view/" + saved.getId(),
                "pencil-square"     // icon cập nhật
            );
        }

        return "redirect:/notification/all";
    }

    // 🔍 Xem chi tiết
    @GetMapping("/view/{id}")
    public String viewNotification(@PathVariable("id") Long id, Model model) {
        Notification noti = notificationRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
        model.addAttribute("notification", noti);
        return "notification/notification_view";
    }

    // ❌ Xóa
    @GetMapping("/delete/{id}")
    public String deleteNotification(@PathVariable("id") Long id) {
        Notification noti = notificationRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid ID: " + id));
        notificationRepo.deleteById(id);

        activityService.log(
            "Deleted announcement '" + noti.getTitle() + "'",
            "/notification/all",
            "calendar-event"               // icon xóa
        );

        return "redirect:/notification/all";
    }
    
    public List<Notification> findTop3ByStatusOrderByPostDateDesc(String status) {
        return repo.findTop3ByStatusOrderByPostDateDesc(status);
    }
    @GetMapping("/announcements")
    public String showAnnouncements(Model model) {
        List<Notification> announcements = notificationRepo.findByStatusOrderByPostDateDesc("Published");
        model.addAttribute("notifications", announcements);
        model.addAttribute("page", "announcements"); // để active menu
        model.addAttribute("pageTitle", "Thông báo");
        return "user/notification/announcements";
    }

   

}
