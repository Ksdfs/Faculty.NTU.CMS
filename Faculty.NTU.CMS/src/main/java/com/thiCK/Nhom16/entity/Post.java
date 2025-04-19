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
    @JoinColumn(name = "category_id") // Mapping với cột trong DB
    private CategoryPost category;

    private String author;

    @Column(name = "post_date")
    private LocalDate postDate;

    private String status;

    // Getter & Setter
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
}
