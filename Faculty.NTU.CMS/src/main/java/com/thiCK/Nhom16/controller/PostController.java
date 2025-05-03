package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Post;
import com.thiCK.Nhom16.repository.CategoryPostRepository;
import com.thiCK.Nhom16.repository.PostRepository;
import com.thiCK.Nhom16.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/post")
public class PostController {

    private final PostRepository postRepo;
    private final CategoryPostRepository categoryRepo;
    private final ActivityService activityService;

    @Autowired
    public PostController(PostRepository postRepo,
                          CategoryPostRepository categoryRepo,
                          ActivityService activityService) {
        this.postRepo = postRepo;
        this.categoryRepo = categoryRepo;
        this.activityService = activityService;
    }

    // 1. Hiển thị danh sách Post với filter
    @GetMapping("/all")
    public String listPosts(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String category,
                            @RequestParam(required = false) String status,
                            Model model) {

        List<Post> filteredPosts = postRepo.findAll().stream()
            .filter(p -> keyword == null || keyword.isEmpty()
                      || p.getTitle().toLowerCase().contains(keyword.toLowerCase()))
            .filter(p -> category == null || category.isEmpty()
                      || p.getCategory().getName().equalsIgnoreCase(category))
            .filter(p -> status == null || status.isEmpty()
                      || p.getStatus().equalsIgnoreCase(status))
            .toList();

        model.addAttribute("posts", filteredPosts);
        model.addAttribute("categories", categoryRepo.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        return "post/post_list";
    }

    // 2. Form tạo mới Post
    @GetMapping("/new")
    public String showAddPostForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("categories", categoryRepo.findAll());
        return "post/post_form";
    }

    // 3. Lưu Post (tạo mới hoặc cập nhật)
    @PostMapping("/save")
    public String savePost(@ModelAttribute("post") Post post) {
        boolean isNew = (post.getId() == null);
        Post saved = postRepo.save(post);

        if (isNew) {
            activityService.log(
                "Created Post '" + saved.getTitle() + "'",
                "/post/view/" + saved.getId(),
                "book"            // icon tạo mới
            );
        } else {
            activityService.log(
                "Updated Post '" + saved.getTitle() + "'",
                "/post/view/" + saved.getId(),
                "pencil-square"      // icon cập nhật
            );
        }

        return "redirect:/post/all";
    }

    // 4. Form sửa Post
    @GetMapping("/edit/{id}")
    public String editPost(@PathVariable("id") Long id, Model model) {
        Post post = postRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
        model.addAttribute("post", post);
        model.addAttribute("categories", categoryRepo.findAll());
        return "post/post_form";
    }

    // 5. Xem chi tiết Post
    @GetMapping("/view/{id}")
    public String viewPost(@PathVariable("id") Long id, Model model) {
        Post post = postRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
        model.addAttribute("post", post);
        return "post/post_view";
    }

    // 6. Xóa Post
    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Long id) {
        Post p = postRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
        postRepo.delete(p);

        activityService.log(
            "Deleted Post '" + p.getTitle() + "'",
            "/post/all",
            "book"               // icon xóa
        );

        return "redirect:/post/all";
    }
}
