package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Event;
import com.thiCK.Nhom16.entity.Activity;
import com.thiCK.Nhom16.repository.PageRepository;
import com.thiCK.Nhom16.repository.PostRepository;
import com.thiCK.Nhom16.repository.NotificationRepository;
import com.thiCK.Nhom16.repository.EventRepository;
import com.thiCK.Nhom16.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired private PageRepository pageRepo;
    @Autowired private PostRepository postRepo;
    @Autowired private NotificationRepository notiRepo;
    @Autowired private EventRepository eventRepo;
    @Autowired private ActivityService activityService;

    @GetMapping
    public String showDashboard(Model model) {
        // Tổng số Pages
        long totalPages = pageRepo.count();
        // Tổng số Posts
        long totalPosts = postRepo.count();
        // Số Posts tạo trong tháng này
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        long newPostsThisMonth = postRepo.countByPostDateBetween(firstOfMonth, today);

        // Tổng thông báo & số thông báo đang ở trạng thái "Published"
        long totalAnnouncements = notiRepo.count();
        long activeAnnouncements = notiRepo.findByStatus("Published").size();

        // Sự kiện sắp tới
        List<Event> allEvents = eventRepo.findAll();
        long upcomingEventsCount = allEvents.stream()
            .filter(e -> e.getStartDate().isAfter(today))
            .count();
        Optional<Event> nextEvent = allEvents.stream()
            .filter(e -> !e.getStartDate().isBefore(today))
            .min(Comparator.comparing(Event::getStartDate));
        long daysToNext = nextEvent
            .map(e -> ChronoUnit.DAYS.between(today, e.getStartDate()))
            .orElse(0L);

        // Lấy 5 hoạt động gần đây nhất
        List<Activity> recentActivities = activityService.getRecentActivities();

        // Đưa vào model
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("newPagesThisMonth", 0); // cập nhật khi Page có createdDate
        model.addAttribute("totalPosts", totalPosts);
        model.addAttribute("newPostsThisMonth", newPostsThisMonth);
        model.addAttribute("totalAnnouncements", totalAnnouncements);
        model.addAttribute("activeAnnouncements", activeAnnouncements);
        model.addAttribute("upcomingEventsCount", upcomingEventsCount);
        model.addAttribute("daysToNextEvent", daysToNext);
        model.addAttribute("recentActivities", recentActivities);

        return "dashboard/dashboard";
    }

    // Hiển thị tất cả các hoạt động
    @GetMapping("/activity_all")
    public String showAllActivities(Model model) {
        List<Activity> allActivities = activityService.getAllActivities();
        model.addAttribute("activities", allActivities);
        return "dashboard/activity_all";
    }
}
