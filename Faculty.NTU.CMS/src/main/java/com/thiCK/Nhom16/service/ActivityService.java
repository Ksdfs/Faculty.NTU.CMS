package com.thiCK.Nhom16.service;

import com.thiCK.Nhom16.entity.Activity;
import com.thiCK.Nhom16.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {
    @Autowired private ActivityRepository activityRepo;

    // Ghi lại hoạt động mới
    public void log(String action, String url, String icon) {
        Activity act = new Activity(action, url, icon, LocalDateTime.now());
        activityRepo.save(act);
    }

    // Lấy 5 hoạt động gần đây nhất
    public List<Activity> getRecentActivities() {
        return activityRepo.findTop5ByOrderByTimeDesc();
    }

    // Lấy tất cả các hoạt động
    public List<Activity> getAllActivities() {
        return activityRepo.findAll();  // Trả về danh sách tất cả hoạt động
    }

    // Xóa hoạt động theo ID
    public void deleteById(Long id) {
        activityRepo.deleteById(id);
    }
}
