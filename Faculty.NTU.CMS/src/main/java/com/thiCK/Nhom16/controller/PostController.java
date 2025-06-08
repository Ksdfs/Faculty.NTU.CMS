package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.CategoryPost;
import com.thiCK.Nhom16.entity.Post;
import com.thiCK.Nhom16.entity.Admin;
import com.thiCK.Nhom16.entity.QuanTriVien;
import com.thiCK.Nhom16.repository.CategoryPostRepository;
import com.thiCK.Nhom16.repository.PostRepository;
import com.thiCK.Nhom16.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.util.StringUtils;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

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
	public PostController(PostRepository postRepo, CategoryPostRepository categoryRepo,
			ActivityService activityService) {
		this.postRepo = postRepo;
		this.categoryRepo = categoryRepo;
		this.activityService = activityService;
	}

	@GetMapping("/all")
	public String listPosts(@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String category, @RequestParam(required = false) String status,
			Model model) {

		List<Post> filteredPosts = postRepo.findAll().stream()
				.filter(p -> keyword == null || keyword.isEmpty()
						|| p.getTitle().toLowerCase().contains(keyword.toLowerCase()))
				.filter(p -> category == null || category.isEmpty()
						|| p.getCategory().getName().equalsIgnoreCase(category))
				.filter(p -> status == null || status.isEmpty() || p.getStatus().equalsIgnoreCase(status)).toList();

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
	public String savePost(@ModelAttribute("post") @Valid Post post, BindingResult result,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
			@RequestParam(value = "thumbnailFiles", required = false) MultipartFile[] thumbnailFiles,
			HttpSession session, Model model) throws IOException {
		// Nếu có lỗi validate, trả lại form
		if (result.hasErrors()) {
			model.addAttribute("categories", categoryRepo.findAll());
			return "post/post_form";
		}

		Object currentUser = session.getAttribute("user");

		// Xử lý Category
		if (post.getCategory() != null && post.getCategory().getId() != null) {
			CategoryPost category = categoryRepo.findById(post.getCategory().getId().longValue()).orElseThrow(
					() -> new IllegalArgumentException("Invalid category ID: " + post.getCategory().getId()));
			post.setCategory(category);
		}

		// Xử lý ngày đăng
		if (post.getId() != null) {
			Post existing = postRepo.findById(post.getId())
					.orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + post.getId()));
			post.setPostDate(existing.getPostDate());
		} else if (post.getPostDate() == null) {
			post.setPostDate(LocalDate.now());
		}

		// Xử lý quyền user
		if (currentUser instanceof Admin) {
			if (post.getStatus() == null || post.getStatus().isEmpty()) {
				post.setStatus("Published");
			}
		} else if (currentUser instanceof QuanTriVien) {
			if (post.getId() == null) {
				post.setStatus("Pending Review");
			} else {
				Post existing = postRepo.findById(post.getId()).orElse(null);
				if (existing != null
						&& ("Draft".equals(existing.getStatus()) || "Rejected".equals(existing.getStatus()))) {
					post.setStatus("Pending Review");
				} else if (existing != null && "Published".equals(existing.getStatus())) {
					throw new IllegalStateException("Cannot edit published posts. Please contact admin.");
				}
			}
			QuanTriVien qtv = (QuanTriVien) currentUser;
			post.setAuthor(qtv.getUsername());
			post.setCreatedByQtv(qtv);
		} else {
			post.setStatus("Draft");
		}

		// XỬ LÝ ẢNH CHÍNH
		if (imageFile != null && !imageFile.isEmpty()) {
			String filename = saveFile(imageFile);
			// chỉ lưu tên file
			post.setImage(filename);
		}

		// XỬ LÝ THUMBNAILS
		if (thumbnailFiles != null && thumbnailFiles.length > 0) {
			List<String> thumbs = new ArrayList<>();
			for (MultipartFile thumb : thumbnailFiles) {
				if (!thumb.isEmpty()) {
					String fn = saveFile(thumb);
					thumbs.add(fn);
				}
			}
			// lưu chuỗi "a.jpg,b.png,…"
			post.setThumbnail(String.join(",", thumbs));
		}

		// Nếu status có dấu phẩy, chỉ giữ phần trước dấu phẩy
		if (post.getStatus() != null && post.getStatus().contains(",")) {
			post.setStatus(post.getStatus().split(",")[0].trim());
		}

		// Lưu Post vào CSDL
		boolean isNew = (post.getId() == null);
		Post saved = postRepo.save(post);

		// Ghi log activity
		if (isNew) {
			if ("Pending Review".equals(saved.getStatus())) {
				activityService.log("Created post '" + saved.getTitle() + "' and submitted for review",
						"/post/view/" + saved.getId(), "clock");
			} else {
				activityService.log("Created post '" + saved.getTitle() + "'", "/post/view/" + saved.getId(), "book");
			}
		} else {
			activityService.log("Updated post '" + saved.getTitle() + "'", "/post/view/" + saved.getId(),
					"pencil-square");
		}

		return "redirect:/post/all";
	}

	private String saveFile(MultipartFile file) throws IOException {
		// làm sạch tên gốc để tránh path traversal
		String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
		// thêm timestamp để tránh trùng tên
		String filename = System.currentTimeMillis() + "_" + originalFilename;

		// đường dẫn đến thư mục uploads ngoài thư mục resources
		Path uploadPath = Paths.get("uploads");
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		// copy file vào thư mục, ghi đè nếu đã tồn tại
		try (InputStream in = file.getInputStream()) {
			Files.copy(in, uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
		}

		return filename;
	}


	@GetMapping("/edit/{id}")
	public String editPost(@PathVariable("id") Long id, Model model) {
		Post post = postRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
		model.addAttribute("post", post);
		model.addAttribute("categories", categoryRepo.findAll());
		return "post/post_form";
	}

	@GetMapping("/post/view/{id}")
	public String viewPost(@PathVariable("id") Long id, Model model) {
		Post post = postRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
		model.addAttribute("post", post);

		// ==== MỚI: Tách chuỗi thumbnail thành List<String> ====
		List<String> thumbs = Collections.emptyList();
		if (post.getThumbnail() != null && !post.getThumbnail().isBlank()) {
			thumbs = Arrays.stream(post.getThumbnail().split(",")).map(String::trim).toList();
		}
		model.addAttribute("thumbnails", thumbs);
		// ================================================

		return "user/post/post_view_user";
	}

	@GetMapping("/delete/{id}")
	public String deletePost(@PathVariable("id") Long id) {
		Post p = postRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
		postRepo.delete(p);
		activityService.log("Deleted Post '" + p.getTitle() + "'", "/post/all", "book");
		return "redirect:/post/all";
	}
}
