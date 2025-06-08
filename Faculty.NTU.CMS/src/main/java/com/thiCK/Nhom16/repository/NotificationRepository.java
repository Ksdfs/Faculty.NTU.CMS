package com.thiCK.Nhom16.repository;

import com.thiCK.Nhom16.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Lấy tất cả theo status
    List<Notification> findByStatus(String status);

    // Lấy top 3 theo postDate desc, chỉ status nào bạn muốn
    List<Notification> findTop3ByStatusOrderByPostDateDesc(String status);
    List<Notification> findByStatusOrderByPostDateDesc(String status);

}
