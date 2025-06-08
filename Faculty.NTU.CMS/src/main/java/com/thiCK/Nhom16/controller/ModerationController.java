package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Admin;
import com.thiCK.Nhom16.entity.Post;
import com.thiCK.Nhom16.repository.PostRepository;
import com.thiCK.Nhom16.service.ActivityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/moderation")
public class ModerationController {

    private final PostRepository postRepo;
    private final ActivityService activityService;

    @Autowired
    public ModerationController(PostRepository postRepo, ActivityService activityService) {
        this.postRepo = postRepo;
        this.activityService = activityService;
    }

    private boolean isAdmin(HttpSession session) {
        Object currentUser = session.getAttribute("user");
        return currentUser instanceof Admin;
    }

    @GetMapping("/posts")
    public String moderationPosts(@RequestParam(name = "status", required = false) String status,
                                  Model model, HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/dashboard?error=access_denied";
        }

        List<Post> posts;
        if (status == null || status.isEmpty()) {
            posts = postRepo.findByStatusInOrderByCreatedAtDesc(
                Arrays.asList("Pending Review", "Draft", "Published", "Rejected")
            );
        } else {
            posts = postRepo.findByStatusOrderByCreatedAtDesc(status);
        }

        long pendingCount = postRepo.countByStatus("Pending Review");
        long draftCount = postRepo.countByStatus("Draft");
        long publishedCount = postRepo.countByStatus("Published");
        long rejectedCount = postRepo.countByStatus("Rejected");

        model.addAttribute("posts", posts);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("draftCount", draftCount);
        model.addAttribute("publishedCount", publishedCount);
        model.addAttribute("rejectedCount", rejectedCount);

        return "moderation/post_moderation";
    }

    @GetMapping("/posts/{id}")
    public String viewPostForModeration(@PathVariable("id") Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/dashboard?error=access_denied";
        }

        Post post = postRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));

        model.addAttribute("post", post);
        return "moderation/post_review";
    }

    @PostMapping("/posts/{id}/approve")
    public String approvePost(@PathVariable("id") Long id,
                              @RequestParam(required = false) String comment,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {

        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied. Only admins can approve posts.");
            return "redirect:/dashboard";
        }

        Post post = postRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));

        String oldStatus = post.getStatus();
        post.setStatus("Published");

        Admin admin = (Admin) session.getAttribute("user");
        post.setCreatedByAdmin(admin);

        postRepo.save(post);

        activityService.log(
            "Approved post '" + post.getTitle() + "' by " + admin.getUsername() + " (from " + oldStatus + " to Published)",
            "/post/view/" + post.getId(),
            "check-circle"
        );

        redirectAttributes.addFlashAttribute("successMessage",
            "Post '" + post.getTitle() + "' has been approved and published.");

        return "redirect:/moderation/posts";
    }

    @PostMapping("/posts/{id}/reject")
    public String rejectPost(@PathVariable("id") Long id,
                             @RequestParam(required = false) String comment,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {

        if (!isAdmin(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied. Only admins can reject posts.");
            return "redirect:/dashboard";
        }

        Post post = postRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));

        String oldStatus = post.getStatus();
        post.setStatus("Rejected");

        Admin admin = (Admin) session.getAttribute("user");

        postRepo.save(post);

        activityService.log(
            "Rejected post '" + post.getTitle() + "' by " + admin.getUsername() + " (from " + oldStatus + " to Rejected)" +
                (comment != null && !comment.isEmpty() ? " - Reason: " + comment : ""),
            "/post/view/" + post.getId(),
            "x-circle"
        );

        redirectAttributes.addFlashAttribute("warningMessage",
            "Post '" + post.getTitle() + "' has been rejected." +
                (comment != null && !comment.isEmpty() ? " Reason: " + comment : ""));

        return "redirect:/moderation/posts";
    }

    @PostMapping("/posts/{id}/submit")
    public String submitForReview(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {

        Post post = postRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));

        if (!"Draft".equals(post.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Only draft posts can be submitted for review.");
            return "redirect:/moderation/posts";
        }

        post.setStatus("Pending Review");
        postRepo.save(post);

        activityService.log(
            "Submitted post '" + post.getTitle() + "' for review",
            "/post/view/" + post.getId(),
            "clock"
        );

        redirectAttributes.addFlashAttribute("infoMessage",
            "Post '" + post.getTitle() + "' has been submitted for review.");

        return "redirect:/moderation/posts";
    }

    @PostMapping("/posts/{id}/draft")
    public String moveToDraft(@PathVariable("id") Long id,
                              RedirectAttributes redirectAttributes) {

        Post post = postRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));

        String oldStatus = post.getStatus();
        post.setStatus("Draft");
        postRepo.save(post);

        activityService.log(
            "Moved post '" + post.getTitle() + "' to draft (from " + oldStatus + ")",
            "/post/view/" + post.getId(),
            "file-earmark"
        );

        redirectAttributes.addFlashAttribute("infoMessage",
            "Post '" + post.getTitle() + "' has been moved to draft.");

        return "redirect:/moderation/posts";
    }
}
