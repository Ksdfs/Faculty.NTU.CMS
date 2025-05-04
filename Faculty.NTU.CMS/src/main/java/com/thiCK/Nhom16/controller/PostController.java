package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Post;
import com.thiCK.Nhom16.enitity.CategoryPost;
import com.thiCK.Nhom16.repository.CategoryPostRepository;
import com.thiCK.Nhom16.repository.PostRepository;
import com.thiCK.Nhom16.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/post")
public class PostController {

    private final PostRepository postRepo;
    private final CategoryPostRepository categoryRepo;
    private final ActivityService activityService;
    private final String uploadDir = "src/main/resources/static/uploads/";

    @Autowired
    public PostController(PostRepository postRepo,
                          CategoryPostRepository categoryRepo,
                          ActivityService activityService) {
        this.postRepo = postRepo;
        this.categoryRepo = categoryRepo;
        this.activityService = activityService;
    }

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

    @GetMapping("/new")
    public String showAddPostForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("categories", categoryRepo.findAll());
        return "post/post_form";
    }

    @PostMapping("/save")
    public String savePost(@ModelAttribute("post") Post post,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           @RequestParam(value = "thumbnailFiles", required = false) MultipartFile[] thumbnailFiles) throws IOException {

        // ✅ Lấy lại Category từ ID
        if (post.getCategory() != null && post.getCategory().getId() != null) {
        	CategoryPost category = categoryRepo.findById(post.getCategory().getId().longValue())

                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + post.getCategory().getId()));
            post.setCategory(category);
        }

        // ✅ Giữ lại postDate nếu edit, hoặc gán ngày nếu tạo mới
        if (post.getId() != null) {
            Post existing = postRepo.findById(post.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + post.getId()));
            post.setPostDate(existing.getPostDate());
        } else if (post.getPostDate() == null) {
            post.setPostDate(LocalDate.now());
        }

        // ✅ Xử lý ảnh chính
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveFile(imageFile);
            post.setImage("/uploads/" + imagePath);
        }

        // ✅ Xử lý ảnh phụ
        if (thumbnailFiles != null && thumbnailFiles.length > 0) {
            List<String> thumbnails = new ArrayList<>();
            for (MultipartFile thumb : thumbnailFiles) {
                if (!thumb.isEmpty()) {
                    String thumbPath = saveFile(thumb);
                    thumbnails.add("/uploads/" + thumbPath);
                }
            }
            post.setThumbnail(String.join(",", thumbnails));
        }

        // ✅ Làm sạch status
        if (post.getStatus() != null && post.getStatus().contains(",")) {
            post.setStatus(post.getStatus().split(",")[0].trim());
        }

        boolean isNew = (post.getId() == null);
        Post saved = postRepo.save(post);

        if (isNew) {
            activityService.log("Created Post '" + saved.getTitle() + "'", "/post/view/" + saved.getId(), "book");
        } else {
            activityService.log("Updated Post '" + saved.getTitle() + "'", "/post/view/" + saved.getId(), "pencil-square");
        }

        return "redirect:/post/all";
    }

    private String saveFile(MultipartFile file) throws IOException {
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get(uploadDir, filename);
        Files.createDirectories(filepath.getParent());
        Files.write(filepath, file.getBytes());
        return filename;
    }

    @GetMapping("/edit/{id}")
    public String editPost(@PathVariable("id") Long id, Model model) {
        Post post = postRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
        model.addAttribute("post", post);
        model.addAttribute("categories", categoryRepo.findAll());
        return "post/post_form";
    }

    @GetMapping("/view/{id}")
    public String viewPost(@PathVariable("id") Long id, Model model) {
        Post post = postRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
        model.addAttribute("post", post);
        return "post/post_view";
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Long id) {
        Post p = postRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
        postRepo.delete(p);

        activityService.log("Deleted Post '" + p.getTitle() + "'", "/post/all", "book");

        return "redirect:/post/all";
    }
}
