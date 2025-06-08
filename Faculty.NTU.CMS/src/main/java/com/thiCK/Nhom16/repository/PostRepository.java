package com.thiCK.Nhom16.repository;

import com.thiCK.Nhom16.entity.Post;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    long countByPostDateBetween(LocalDate startDate, LocalDate endDate);
    List<Post> findByStatus(String status);
    List<Post> findByStatusOrderByCreatedAtDesc(String status);
    List<Post> findByStatusInOrderByCreatedAtDesc(List<String> statuses);
    long countByStatus(String status);

    // Tìm tối đa 5 bài cùng category (không lấy bài hiện tại)
    List<Post> findTop5ByCategoryIdAndIdNotOrderByPostDateDesc(Long categoryId, Long excludePostId);

    // Lấy tất cả theo category (non-paging)
    List<Post> findByCategoryId(Long categoryId);

    // **Thêm phương thức phân trang theo category**
    Page<Post> findByCategoryId(Long categoryId, Pageable pageable);
    
    
}
