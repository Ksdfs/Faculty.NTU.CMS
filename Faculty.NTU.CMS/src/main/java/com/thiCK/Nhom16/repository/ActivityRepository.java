package com.thiCK.Nhom16.repository;

import com.thiCK.Nhom16.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    // Lấy 5 hoạt động gần đây nhất
    List<Activity> findTop5ByOrderByTimeDesc();

    // Lấy tất cả các hoạt động
    List<Activity> findAll();  // Phương thức này sẽ tự động được cung cấp bởi JpaRepository
    
 // Phương thức mới có sắp xếp
    List<Activity> findAllByOrderByTimeDesc();
}
