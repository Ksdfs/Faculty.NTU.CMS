package com.thiCK.Nhom16.repository;

import com.thiCK.Nhom16.entity.Notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByStatus(String status);
}
