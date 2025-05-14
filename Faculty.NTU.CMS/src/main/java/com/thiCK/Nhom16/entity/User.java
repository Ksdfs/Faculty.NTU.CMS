package com.thiCK.Nhom16.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "Users")
public class User implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;    // ví dụ "ROLE_USER"

    /** 
     * Public no-arg constructor để JPA + các chỗ gọi new User() ngoài package đều thấy được 
     */
    public User() {
    }

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    // === Getter/Setter cho id ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // === UserDetails: username ===
    @Override
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // === Getter/Setter cho email ===
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // === UserDetails: password ===
    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // === Getter/Setter cho role ===
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // === UserDetails override ===
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> role);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
