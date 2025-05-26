package com.thiCK.Nhom16.service;

import com.thiCK.Nhom16.entity.Admin;
import com.thiCK.Nhom16.entity.QuanTriVien;
import com.thiCK.Nhom16.entity.User;
import com.thiCK.Nhom16.repository.AdminRepository;
import com.thiCK.Nhom16.repository.QuanTriVienRepository;
import com.thiCK.Nhom16.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final AdminRepository adminRepo;
    private final QuanTriVienRepository qtvRepo;

    public UserService(UserRepository userRepo,
                       AdminRepository adminRepo,
                       QuanTriVienRepository qtvRepo) {
        this.userRepo = userRepo;
        this.adminRepo = adminRepo;
        this.qtvRepo = qtvRepo;
    }

    /**
     * Đăng ký user mới với role USER, lưu nguyên mật khẩu
     * @return null nếu thành công, hoặc thông báo lỗi
     */
    public String registerUser(User user) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            return "Tên đăng nhập đã tồn tại";
        }
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return "Email đã tồn tại";
        }
        user.setRole("USER");
        userRepo.save(user);
        return null;
    }

    /**
     * Lưu hoặc cập nhật User
     */
    public User save(User user) {
        return userRepo.save(user);
    }

    /**
     * Đăng nhập: kiểm tra tuần tự Admin, QuanTriVien, rồi User
     * @return Admin, QuanTriVien hoặc User nếu thông tin đúng; ngược lại null
     */
    public Object login(String credential, String password) {
        Optional<Admin> adminOpt = adminRepo.findByUsername(credential);
        if (adminOpt.isPresent()) {
            Admin a = adminOpt.get();
            if (a.getPassword().equals(password)) {
                return a;
            }
            return null;
        }

        Optional<QuanTriVien> qtvOpt = qtvRepo.findByUsername(credential);
        if (qtvOpt.isPresent()) {
            QuanTriVien q = qtvOpt.get();
            if (q.getPassword().equals(password)) {
                return q;
            }
            return null;
        }

        Optional<User> userOpt = userRepo.findByUsername(credential);
        if (userOpt.isEmpty()) {
            userOpt = userRepo.findByEmail(credential);
        }
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            if (u.getPassword().equals(password)) {
                return u;
            }
        }

        return null;
    }

    /**
     * Lấy danh sách tất cả Users (loại trừ Admin và QTV)
     */
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    /**
     * Xóa user theo ID
     */
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    /**
     * Kiểm tra mật khẩu cũ của User
     */
    public boolean checkOldPassword(User user, String oldPassword) {
        return user.getPassword().equals(oldPassword);
    }

    /**
     * Thay đổi mật khẩu cho User
     */
    public void changePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        userRepo.save(user);
    }
}
