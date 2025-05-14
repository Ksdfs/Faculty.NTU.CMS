package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.CategoryPost;
import com.thiCK.Nhom16.repository.CategoryPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/category")
public class CategoryPostController {

    @Autowired
    private CategoryPostRepository categoryRepo;

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new CategoryPost());
        return "category/category_form";
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute CategoryPost category) {
        categoryRepo.save(category);
        return "redirect:/post/all";
    }
    
    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable("id") Long id, Model model) {
        CategoryPost category = categoryRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + id));
        model.addAttribute("category", category);
        return "category/category_form"; // Dùng lại giao diện tạo
    }

}
