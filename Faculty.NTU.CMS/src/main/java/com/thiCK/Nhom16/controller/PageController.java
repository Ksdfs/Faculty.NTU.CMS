package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Page;
import com.thiCK.Nhom16.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PageController {

    @Autowired
    private PageRepository pageRepository;

    @GetMapping("/page/all")
    public String listPages(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        List<Page> pages;
        if (keyword != null && !keyword.isEmpty()) {
            pages = pageRepository.findByTitleContainingIgnoreCase(keyword);
        } else {
            pages = pageRepository.findAll();
        }
        model.addAttribute("pages", pages);
        model.addAttribute("keyword", keyword); // để giữ lại input
        return "pages/page_list";
    }

    //Tạo API tìm kiếm
    //Trả JSON danh sách Page tương ứng với từ khoá đang nhập.
    @GetMapping("/page/search")
    @ResponseBody
    public List<Page> searchPages(@RequestParam("keyword") String keyword) {
        return pageRepository.findByTitleContainingIgnoreCase(keyword);
    }
    
    //Them page
    @GetMapping("/page/new")
    public String showAddPageForm(Model model) {
        model.addAttribute("page", new Page());
        return "pages/page_form";  
    }

    @PostMapping("/page/save")
    public String savePage(@ModelAttribute("page") Page page) {
        pageRepository.save(page);
        return "redirect:/page/all";
    }

    
    //Xoa page
    @GetMapping("/page/delete/{id}")
    public String deletePage(@PathVariable("id") Long id) {
        pageRepository.deleteById(id);
        return "redirect:/page/all";
    }
    
    //edit page
    @GetMapping("/page/edit/{id}")
    public String showEditPageForm(@PathVariable("id") Long id, Model model) {
        Page page = pageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid page ID: " + id));
        model.addAttribute("page", page);
        return "pages/page_form";  // dùng lại form cũ để chỉnh sửa
    }

    //view page
    @GetMapping("/page/view/{id}")
    public String viewPage(@PathVariable("id") Long id, Model model) {
        Page page = pageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid page ID: " + id));
        model.addAttribute("page", page);
        return "pages/page_view"; 
    }



}
