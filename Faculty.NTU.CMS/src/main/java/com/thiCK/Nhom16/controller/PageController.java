package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Page;
import com.thiCK.Nhom16.repository.PageRepository;
import com.thiCK.Nhom16.service.ActivityService;
import com.thiCK.Nhom16.service.MenuService;
import java.text.Normalizer;
import org.springframework.data.domain.PageRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/page")
public class PageController {

	@Autowired
	private PageRepository pageRepository;

	@Autowired
	private ActivityService activityService;

	@Autowired // 🆕 inject MenuService
	private MenuService menuService;

	/*----------------------------------------------------
	 * 1. Danh sách Page
	 *---------------------------------------------------*/
	@GetMapping("/all")
	public String listPages(Model model) {
		List<Page> pages = pageRepository.findAll();
		model.addAttribute("pages", pages);
		return "pages/page_list";
	}

	/*----------------------------------------------------
	 * 2. Form tạo mới Page
	 *---------------------------------------------------*/
	@GetMapping("/new")
	public String newPageForm(Model model) {
		model.addAttribute("page", new Page());
		model.addAttribute("allMenus", menuService.getHeaderMenus()); // 🆕 nạp menu cha
		return "pages/page_form";
	}

	/*----------------------------------------------------
	 * 3. Lưu Page  (tạo mới & cập nhật)
	 *---------------------------------------------------*/
	/* ===== SAVE PAGE ===== */
	@PostMapping("/save")
	public String savePage(@ModelAttribute("page") Page page,
			@RequestParam(value = "parentMenuId", required = false) Integer parentMenuId) {

		boolean isNew = (page.getId() == null);

		/* 1️⃣ Tự sinh slug nếu user để trống */
		if (page.getUrl() == null || page.getUrl().isBlank()) {
			String baseSlug = toSlug(page.getTitle()); // chuyển title → slug
			String slug = baseSlug;
			int i = 1;

			// Đảm bảo duy nhất; nếu đang edit, cho phép trùng với chính nó
			while (pageRepository.existsByUrlAndIdNot(slug, page.getId() == null ? -1L : page.getId())) {
				slug = baseSlug + "-" + i++;
			}
			page.setUrl(slug);
		}

		/* 2️⃣ Lưu Page */
		Page saved = pageRepository.save(page);

		/* 3️⃣ Gắn menu cha nếu được chọn */
		if (parentMenuId != null) {
			menuService.attachPageToMenu(saved, parentMenuId);
		}

		/* 4️⃣ Ghi log */
		if (isNew) {
			activityService.log("Created Page '" + saved.getTitle() + "'", "/page/view/" + saved.getId(),
					"file-earmark-text-fill");
		} else {
			activityService.log("Updated Page '" + saved.getTitle() + "'", "/page/view/" + saved.getId(),
					"file-earmark-text-fill");
		}

		return "redirect:/page/all";
	}

	/* ===== HÀM CHUYỂN TIÊU ĐỀ ➜ SLUG ===== */
	private String toSlug(String input) {
		if (input == null)
			return "";

		// Bỏ dấu tiếng Việt
		String noAccent = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "");

		// Thường hóa, gạch ngang, loại ký tự đặc biệt
		return noAccent.toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-").replaceAll("-{2,}", "-")
				.replaceAll("^-|-$", "");
	}

	/*----------------------------------------------------
	 * 4. Form sửa Page
	 *---------------------------------------------------*/
	@GetMapping("/edit/{id}")
	public String editPageForm(@PathVariable("id") Long id, Model model) {
		Page page = pageRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid page ID: " + id));
		model.addAttribute("page", page);
		model.addAttribute("allMenus", menuService.getHeaderMenus()); // 🆕 nạp menu cha
		return "pages/page_form";
	}

	/*----------------------------------------------------
	 * 5. Xem chi tiết Page
	 *---------------------------------------------------*/
	@GetMapping("/view/{id}")
	public String viewPage(@PathVariable("id") Long id, Model model) {
		Page page = pageRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid page ID: " + id));
		model.addAttribute("page", page);
		return "pages/page_view";
	}

	/*----------------------------------------------------
	 * 6. Xóa Page
	 *---------------------------------------------------*/
	@GetMapping("/delete/{id}")
	public String deletePage(@PathVariable("id") Long id) {
		Page page = pageRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid page ID: " + id));

		pageRepository.delete(page);
		activityService.log("Deleted Page '" + page.getTitle() + "'", "/page/all", "file-earmark-text-fill");
		return "redirect:/page/all";
	}

	/*----------------------------------------------------
	 * 7. Trang Giới thiệu (user side)
	 *---------------------------------------------------*/
	@GetMapping("/gioi-thieu")
	public String showAboutPages(Model model) {
		List<Page> pages = pageRepository.findAll();
		model.addAttribute("pages", pages);
		model.addAttribute("page", "about");
		model.addAttribute("pageTitle", "Giới thiệu - Khoa Du Lịch NTU");
		return "user/pages/pages";
	}

	@GetMapping("/{slug:[a-z0-9\\-]+}")
	public String viewPageBySlug(@PathVariable("slug") String slug, Model model) {
		Page page = pageRepository.findByUrl(slug)
				.orElseThrow(() -> new IllegalArgumentException("Page không tồn tại: " + slug));

		List<Page> related = List.of();
		if (page.getMenu() != null) {
			Long menuId = page.getMenu().getId().longValue(); // ép Integer → Long
			related = pageRepository.findRelated(menuId, page.getId(), PageRequest.of(0, 5));
		}

		model.addAttribute("page", page);
		model.addAttribute("relatedPages", related);
		return "user/pages/page_detail";
	}

}
