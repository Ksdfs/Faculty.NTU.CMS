package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Event;
import com.thiCK.Nhom16.entity.Notification;
import com.thiCK.Nhom16.entity.Post;
import com.thiCK.Nhom16.entity.CategoryPost;
import com.thiCK.Nhom16.repository.EventRepository;
import com.thiCK.Nhom16.repository.NotificationRepository;
import com.thiCK.Nhom16.repository.PostRepository;
import com.thiCK.Nhom16.repository.CategoryPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {

	private final PostRepository postRepository;
	private final NotificationRepository notificationRepository;
	private final EventRepository eventRepository;
	private final CategoryPostRepository categoryRepo;

	@Autowired
	public HomeController(PostRepository postRepository, NotificationRepository notificationRepository,
			EventRepository eventRepository, CategoryPostRepository categoryRepo) {
		this.postRepository = postRepository;
		this.notificationRepository = notificationRepository;
		this.eventRepository = eventRepository;
		this.categoryRepo = categoryRepo;
	}

	@GetMapping({ "/", "/index" })
	public String index(Model model) {
		model.addAttribute("pageTitle", "Trang chủ");
		model.addAttribute("page", "home");

		// 1. Bài viết mới
		Pageable top3Posts = PageRequest.of(0, 3, Sort.by("postDate").descending());
		List<Post> latestPosts = postRepository.findAll(top3Posts).getContent();
		model.addAttribute("posts", latestPosts);

		// 2. Thông báo mới
		List<Notification> latestAnnouncements = notificationRepository
				.findTop3ByStatusOrderByPostDateDesc("Published");
		model.addAttribute("announcements", latestAnnouncements);

		// 3. Sự kiện sắp tới
		Pageable top3Events = PageRequest.of(0, 3, Sort.by("startDate").ascending());
		List<Event> upcomingEvents = eventRepository.findAll(top3Events).getContent();
		model.addAttribute("events", upcomingEvents);

		return "user/index";
	}

	// Redirect từ URL admin cũ
	@GetMapping("/post/view/{id}")
	public String redirectPostView(@PathVariable Long id) {
		return "redirect:/tin-tuc/" + id;
	}

	// Xem chi tiết bài viết
	@GetMapping("/tin-tuc/{id}")
	public String viewPostUser(@PathVariable("id") Long id, Model model) {
		// Lấy bài viết
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy post với id = " + id));
		model.addAttribute("post", post);

		// Tách thumbnails thành List<String>
		List<String> thumbs = Collections.emptyList();
		if (post.getThumbnail() != null && !post.getThumbnail().isBlank()) {
			thumbs = Arrays.stream(post.getThumbnail().split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
		}
		model.addAttribute("thumbnails", thumbs);

		// Bài viết liên quan (khác id, cùng category)
		Long catId = post.getCategory().getId().longValue();
		List<Post> related = postRepository.findTop5ByCategoryIdAndIdNotOrderByPostDateDesc(catId, id);
		model.addAttribute("relatedPosts", related);

		// Lấy danh sách danh mục
		List<CategoryPost> categories = categoryRepo.findAll();
		model.addAttribute("categories", categories);

		return "user/post/post_view_user";
	}

	@GetMapping("/tin-tuc")
	public String showNewsPage(@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) Long category, Model model) {
		// thiết lập pageTitle, page
		model.addAttribute("pageTitle", "Tin tức - Khoa Du Lịch NTU");
		model.addAttribute("page", "posts");

		// lấy danh mục
		List<CategoryPost> categories = categoryRepo.findAll();
		model.addAttribute("categories", categories);

		// phân trang 9 bài/trang, sắp theo postDate desc
		Pageable pageable = PageRequest.of(page - 1, 9, Sort.by("postDate").descending());
		Page<Post> postPage;
		if (category != null) {
			postPage = postRepository.findByCategoryId(category, pageable);
		} else {
			postPage = postRepository.findAll(pageable);
		}
		model.addAttribute("posts", postPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", postPage.getTotalPages());

		// recent 5 bài mới nhất
		Pageable recentPg = PageRequest.of(0, 5, Sort.by("postDate").descending());
		List<Post> recentPosts = postRepository.findAll(recentPg).getContent();
		model.addAttribute("recentPosts", recentPosts);

		return "user/post/post_user";
	}

}
