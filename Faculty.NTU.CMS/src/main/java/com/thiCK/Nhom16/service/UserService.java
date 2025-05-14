package com.thiCK.Nhom16.service;

import com.thiCK.Nhom16.entity.Admin;
import com.thiCK.Nhom16.entity.QuanTriVien;
import com.thiCK.Nhom16.entity.User;
import com.thiCK.Nhom16.repository.AdminRepository;
import com.thiCK.Nhom16.repository.QuanTriVienRepository;
import com.thiCK.Nhom16.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired private UserRepository userRepo;
    @Autowired private AdminRepository adminRepo;
    @Autowired private QuanTriVienRepository qtvRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    // Đăng ký user mới với role USER
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        return userRepo.save(user);
    }

    // Lưu/ cập nhật User
    public User save(User user) {
        return userRepo.save(user);
    }

    // Lấy tất cả user (dành cho admin)
    public List<User> findAll() {
        return userRepo.findAll();
    }

    // Xóa user theo ID (dành cho admin)
    public void deleteById(Long id) {
        userRepo.deleteById(id);
    }

    // Kiểm tra mật khẩu cũ có đúng không
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    // Thay đổi mật khẩu
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    /**
     * Lấy UserDetails cho Spring Security.
     * Thứ tự tìm: Admin -> QuanTriVien -> User
     * Gán authority tương ứng ROLE_ADMIN, ROLE_QUANTRIVIEN, ROLE_USER
     */
    @Override
    public UserDetails loadUserByUsername(String credential)
            throws UsernameNotFoundException {
        // 1) Admin?
        if (adminRepo.existsByUsername(credential)) {
            Admin a = adminRepo.findByUsername(credential)
                .orElseThrow(() -> new UsernameNotFoundException("Admin không tồn tại"));
            return new org.springframework.security.core.userdetails.User(
                a.getUsername(), a.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }
        // 2) QuanTriVien?
        if (qtvRepo.existsByUsername(credential)) {
            QuanTriVien q = qtvRepo.findByUsername(credential)
                .orElseThrow(() -> new UsernameNotFoundException("Quản trị viên không tồn tại"));
            return new org.springframework.security.core.userdetails.User(
                q.getUsername(), q.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_QUANTRIVIEN"))
            );
        }
        // 3) User bình thường
        User u = userRepo.findByUsername(credential)
            .or(() -> userRepo.findByEmail(credential))
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + credential));
        return new org.springframework.security.core.userdetails.User(
            u.getUsername(), u.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole()))
        );
    }

    // Lấy User entity theo username (nếu cần)
    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));
    }

    // Lấy User entity theo email (nếu cần)
    public User findByEmail(String email) {
        return userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + email));
    }
}
