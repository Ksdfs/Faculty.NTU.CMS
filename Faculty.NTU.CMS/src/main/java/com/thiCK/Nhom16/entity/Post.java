package com.thiCK.Nhom16.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.thiCK.Nhom16.enitity.CategoryPost;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryPost category;

    private String author;

    @Column(name = "post_date")
    private LocalDate postDate;

    private String status;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String excerpt;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private String tags;

    private String image;      // ảnh chính
    private String thumbnail;  // ảnh phụ

    // === Getter & Setter ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public CategoryPost getCategory() { return category; }
    public void setCategory(CategoryPost category) { this.category = category; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public LocalDate getPostDate() { return postDate; }
    public void setPostDate(LocalDate postDate) { this.postDate = postDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
}
