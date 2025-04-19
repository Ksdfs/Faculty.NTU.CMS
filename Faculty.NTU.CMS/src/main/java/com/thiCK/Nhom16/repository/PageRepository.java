package com.thiCK.Nhom16.repository;

import com.thiCK.Nhom16.entity.Page;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, Long> {
	List<Page> findByTitleContainingIgnoreCase(String keyword); // tìm theo tiêu đề, không phân biệt hoa thường
}
