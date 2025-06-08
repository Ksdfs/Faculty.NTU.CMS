package com.thiCK.Nhom16.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "page")
public class Page {

    /* ====== Khóa chính ====== */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ====== Thông tin cơ bản ====== */
    private String title;
    private String url;

    /* Nội dung chi tiết (nvarchar(max) trong SQL) */
    @Column(columnDefinition = "nvarchar(max)")
    private String content;

    /* Ngày phát hành (publish_date) */
    @Column(name = "publish_date")
    private LocalDateTime publishDate;

    /* Cột cũ: last_updated (giữ lại để tránh lỗi) */
    @Column(name = "last_updated")
    private String lastUpdated;

    private String status;   // Draft / Published / ...

    /* ====== Thời gian hệ thống ====== */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* ====== Quan hệ người tạo ====== */
    @ManyToOne
    @JoinColumn(name = "created_by_admin_id")
    private Admin createdByAdmin;

    @ManyToOne
    @JoinColumn(name = "created_by_qtv_id")
    private QuanTriVien createdByQtv;

    /* ====== Quan hệ Menu (menu_id) ====== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    /* ====== Getters & Setters ====== */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDateTime publishDate) { this.publishDate = publishDate; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Admin getCreatedByAdmin() { return createdByAdmin; }
    public void setCreatedByAdmin(Admin createdByAdmin) { this.createdByAdmin = createdByAdmin; }

    public QuanTriVien getCreatedByQtv() { return createdByQtv; }
    public void setCreatedByQtv(QuanTriVien createdByQtv) { this.createdByQtv = createdByQtv; }

    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }
}
