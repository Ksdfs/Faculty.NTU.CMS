package com.thiCK.Nhom16.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Activity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;       // Ví dụ: "Created Page 'About Us'"
    private String url;          // Đường link để View chi tiết
    private String icon;         // Icon bootstrap, ví dụ "pen-fill", "plus-lg", "chat-left-text", "calendar-event"
    private LocalDateTime time;  // Thời gian tạo

    public Activity() {}

    public Activity(String action, String url, String icon, LocalDateTime time) {
        this.action = action;
        this.url = url;
        this.icon = icon;
        this.time = time;
    }

    // === getters & setters ===
    public Long getId() { return id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
}
