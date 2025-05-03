package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Page;
import com.thiCK.Nhom16.repository.PageRepository;
import com.thiCK.Nhom16.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/page")
public class PageController {

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private ActivityService activityService;

    // 1. Danh sách Page
    @GetMapping("/all")
    public String listPages(Model model) {
        List<Page> pages = pageRepository.findAll();
        model.addAttribute("pages", pages);
        return "pages/page_list";
    }

    // 2. Form tạo mới Page
    @GetMapping("/new")
    public String newPageForm(Model model) {
        model.addAttribute("page", new Page());
        return "pages/page_form";
    }

    // 3. Lưu Page (cả tạo mới và cập nhật)
    @PostMapping("/save")
    public String savePage(@ModelAttribute("page") Page page) {
        boolean isNew = (page.getId() == null);
        Page saved = pageRepository.save(page);

        if (isNew) {
            activityService.log(
                "Created Page '" + saved.getTitle() + "'",
                "/page/view/" + saved.getId(),
                "file-earmark-text-fill"
            );
        } else {
            activityService.log(
                "Updated Page '" + saved.getTitle() + "'",
                "/page/view/" + saved.getId(),
                "file-earmark-text-fill"
            );
        }

        return "redirect:/page/all";
    }

    // 4. Form sửa Page
    @GetMapping("/edit/{id}")
    public String editPageForm(@PathVariable("id") Long id, Model model) {
        Page page = pageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid page ID: " + id));
        model.addAttribute("page", page);
        return "pages/page_form";
    }

    // 5. Xem chi tiết Page
    @GetMapping("/view/{id}")
    public String viewPage(@PathVariable("id") Long id, Model model) {
        Page page = pageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid page ID: " + id));
        model.addAttribute("page", page);
        return "pages/page_view";
    }

    // 6. Xóa Page
    @GetMapping("/delete/{id}")
    public String deletePage(@PathVariable("id") Long id) {
        Page page = pageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid page ID: " + id));

        pageRepository.delete(page);
        activityService.log(
            "Deleted Page '" + page.getTitle() + "'",
            "/page/all",
            "file-earmark-text-fill"
        );

        return "redirect:/page/all";
    }
}
