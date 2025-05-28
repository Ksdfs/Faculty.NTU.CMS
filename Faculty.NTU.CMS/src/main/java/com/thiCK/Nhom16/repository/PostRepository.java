package com.thiCK.Nhom16.repository;

import com.thiCK.Nhom16.entity.Post;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
	long countByPostDateBetween(LocalDate startDate, LocalDate endDate);
	List<Post> findByStatus(String status);
    List<Post> findByStatusOrderByCreatedAtDesc(String status);
    List<Post> findByStatusInOrderByCreatedAtDesc(List<String> statuses);
    long countByStatus(String status);

}

