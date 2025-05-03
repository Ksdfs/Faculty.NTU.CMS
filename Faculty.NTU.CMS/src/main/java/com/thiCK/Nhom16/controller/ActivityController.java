package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Activity; // ✅ THÊM DÒNG NÀY để tránh lỗi "Activity cannot be resolved"
import com.thiCK.Nhom16.service.ActivityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ✅ THAY dòng logback sai bằng dòng này
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List; // ✅ BỔ SUNG để dùng List<Activity>

@Controller
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/activity/delete/{id}")
    public String deleteActivity(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        activityService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Xóa hoạt động thành công!");
        return "redirect:/dashboard";
    }

    @GetMapping("/activity/all")
    public String viewAllActivities(Model model) {
        List<Activity> activities = activityService.getAllActivities();
        model.addAttribute("activities", activities);
        return "activity_all";
    }
}
