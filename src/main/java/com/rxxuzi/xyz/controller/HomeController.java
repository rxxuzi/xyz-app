package com.rxxuzi.xyz.controller;

import com.rxxuzi.xyz.entity.Post;
import com.rxxuzi.xyz.entity.User;
import com.rxxuzi.xyz.service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private PostService postService;

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "foryou") String tab) {
        User currentUser = (User) session.getAttribute("user");

        List<Post> posts;
        if (currentUser != null && "following".equals(tab)) {
            // Show posts from users they follow
            posts = postService.getFollowingTimeline(currentUser.getId(), currentUser.getId(), page, 20);
            model.addAttribute("user", currentUser);
        } else {
            // Show public timeline (For You)
            posts = postService.getPublicTimeline(currentUser != null ? currentUser.getId() : null, page, 20);
            if (currentUser != null) {
                model.addAttribute("user", currentUser);
            }
        }

        model.addAttribute("posts", posts);
        model.addAttribute("page", page);
        model.addAttribute("currentTab", tab);
        model.addAttribute("currentPage", "home");
        return "home";
    }

    @PostMapping("/post")
    public String createPost(@RequestParam String content,
                             @RequestParam(required = false) Long parentPostId,
                             @RequestParam(required = false) Long quotedPostId,
                             HttpSession session,
                             Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            postService.createPost(currentUser.getId(), content, parentPostId, quotedPostId);
            if (parentPostId != null) {
                return "redirect:/post/" + parentPostId;
            }
            return "redirect:/home";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return home(model, session, 0, "foryou");
        }
    }

    @PostMapping("/post/{id}/like")
    @ResponseBody
    public String likePost(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "{\"success\": false, \"message\": \"Not logged in\"}";
        }

        boolean success = postService.likePost(id, currentUser.getId());
        return "{\"success\": " + success + "}";
    }

    @PostMapping("/post/{id}/unlike")
    @ResponseBody
    public String unlikePost(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "{\"success\": false, \"message\": \"Not logged in\"}";
        }

        boolean success = postService.unlikePost(id, currentUser.getId());
        return "{\"success\": " + success + "}";
    }

    @PostMapping("/post/{id}/delete")
    @ResponseBody
    public String deletePost(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "{\"success\": false, \"message\": \"Not logged in\"}";
        }

        boolean success = postService.deletePost(id, currentUser.getId());
        if (success) {
            return "{\"success\": true, \"redirect\": \"/home\"}";
        }
        return "{\"success\": false}";
    }
}