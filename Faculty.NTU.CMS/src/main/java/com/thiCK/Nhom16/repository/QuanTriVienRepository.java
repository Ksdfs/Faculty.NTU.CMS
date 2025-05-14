package com.thiCK.Nhom16.repository;

import com.thiCK.Nhom16.entity.QuanTriVien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface QuanTriVienRepository extends JpaRepository<QuanTriVien, Integer> {
    Optional<QuanTriVien> findByUsername(String username);
    boolean existsByUsername(String username);
}
