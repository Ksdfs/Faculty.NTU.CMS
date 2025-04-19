package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Post;
import com.thiCK.Nhom16.repository.PostRepository;
import com.thiCK.Nhom16.repository.CategoryPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {

    private final PostRepository postRepo;
    private final CategoryPostRepository categoryRepo;

    @Autowired
    public PostController(PostRepository postRepo, CategoryPostRepository categoryRepo) {
        this.postRepo = postRepo;
        this.categoryRepo = categoryRepo;
    }

    @GetMapping("/post/all")
    public String listPosts(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String category,
                            @RequestParam(required = false) String status,
                            Model model) {

        List<Post> filteredPosts = postRepo.findAll().stream()
            .filter(p -> keyword == null || keyword.isEmpty() || p.getTitle().toLowerCase().contains(keyword.toLowerCase()))
            .filter(p -> category == null || category.isEmpty() || p.getCategory().getName().equalsIgnoreCase(category))
            .filter(p -> status == null || status.isEmpty() || p.getStatus().equalsIgnoreCase(status))
            .toList();

        model.addAttribute("posts", filteredPosts);
        model.addAttribute("categories", categoryRepo.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        return "post/post_list";
    }

    // Hiển thị form tạo mới bài viết
    @GetMapping("/post/new")
    public String showAddPostForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("categories", categoryRepo.findAll());
        return "post/post_form";
    }

    // Lưu bài viết mới
    @PostMapping("/post/save")
    public String savePost(@ModelAttribute("post") Post post) {
        postRepo.save(post);
        return "redirect:/post/all";
    }

    // Xóa bài viết
    @GetMapping("/post/delete/{id}")
    public String deletePost(@PathVariable("id") Long id) {
        postRepo.deleteById(id);
        return "redirect:/post/all";
    }

    // Xem chi tiết bài viết
    @GetMapping("/post/view/{id}")
    public String viewPost(@PathVariable("id") Long id, Model model) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
        model.addAttribute("post", post);
        return "post/post_view";
    }

    // Sửa bài viết (bổ sung nếu cần)
    @GetMapping("/post/edit/{id}")
    public String editPost(@PathVariable("id") Long id, Model model) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
        model.addAttribute("post", post);
        model.addAttribute("categories", categoryRepo.findAll());
        return "post/post_form";
    }
}
