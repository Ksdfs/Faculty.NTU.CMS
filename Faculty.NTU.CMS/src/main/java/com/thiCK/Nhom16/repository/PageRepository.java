package com.thiCK.Nhom16.repository;

import com.thiCK.Nhom16.entity.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Long> {

    // Tìm theo tiêu đề (không phân biệt hoa thường)
    List<Page> findByTitleContainingIgnoreCase(String keyword);

    // Kiểm tra tồn tại slug khác bản thân
    boolean existsByUrlAndIdNot(String url, Long id);

    // Lấy Page theo slug
    Optional<Page> findByUrl(String url);

    // Lấy tối đa 5 trang cùng menu cha (trừ current), sắp xếp theo updatedAt DESC
    @Query("SELECT p FROM Page p " +
           "WHERE p.menu.id = :menuId AND p.id <> :currentId " +
           "ORDER BY p.updatedAt DESC")
    List<Page> findRelated(@Param("menuId") Long menuId,
                           @Param("currentId") Long currentId,
                           Pageable pageable);
}
