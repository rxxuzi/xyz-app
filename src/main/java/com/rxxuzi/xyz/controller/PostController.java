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
import java.util.Objects;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/post/{id}")
    public String postDetail(@PathVariable Long id,
                             @RequestParam(defaultValue = "0") int page,
                             Model model,
                             HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Long currentUserId = currentUser != null ? currentUser.getId() : null;

        Post post = postService.getPost(id, currentUserId);
        if (post == null) {
            model.addAttribute("message", "This post doesn't exist or has been deleted");
            return "404";
        }

        // Get parent chain (posts that this post is replying to)
        List<Post> parentChain = postService.getReplyChain(id, currentUserId);

        // Get replies to this post
        List<Post> replies = postService.getReplies(id, currentUserId, page, 20);

        model.addAttribute("post", post);
        model.addAttribute("parentChain", parentChain);
        model.addAttribute("replies", replies);
        model.addAttribute("page", page);
        model.addAttribute("currentUserId", Objects.requireNonNull(currentUserId));

        return "post-detail";
    }

    @PostMapping("/post/{id}/reply")
    public String createReply(@PathVariable Long id,
                              @RequestParam String content,
                              HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            postService.createPost(currentUser.getId(), content, id, null);
            return "redirect:/post/" + id;
        } catch (IllegalArgumentException e) {
            return "redirect:/post/" + id + "?error=" + e.getMessage();
        }
    }
}