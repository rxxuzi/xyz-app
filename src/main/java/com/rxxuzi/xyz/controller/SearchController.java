package com.rxxuzi.xyz.controller;

import com.rxxuzi.xyz.entity.Post;
import com.rxxuzi.xyz.entity.User;
import com.rxxuzi.xyz.service.PostService;
import com.rxxuzi.xyz.service.UserService;
import com.rxxuzi.xyz.security.XssSanitizer;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private XssSanitizer xssSanitizer;

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q,
                         @RequestParam(defaultValue = "posts") String type,
                         @RequestParam(defaultValue = "0") int page,
                         Model model,
                         HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Long currentUserId = currentUser != null ? currentUser.getId() : null;

        if (q != null) {
            q = q.trim();
        }

        model.addAttribute("query", q);
        model.addAttribute("type", type);
        model.addAttribute("page", page);

        if (q == null || q.isEmpty()) {
            return "search";
        }

        if (q.startsWith("#")) {
            List<Post> posts = postService.getPostsByHashtag(q, currentUserId, page, 20);
            model.addAttribute("posts", posts);
            model.addAttribute("type", "hashtag");
        } else if ("users".equals(type)) {
            List<User> users = userService.searchUsers(q, page, 20);
            model.addAttribute("users", users);
        } else {
            List<Post> posts = postService.searchPosts(q, currentUserId, page, 20);
            model.addAttribute("posts", posts);
        }

        return "search";
    }

    @GetMapping("/search/suggestions")
    @ResponseBody
    public Map<String, Object> searchSuggestions(@RequestParam String q,
                                                 HttpSession session) {
        Map<String, Object> suggestions = new HashMap<>();

        if (q == null || q.trim().length() < 2) {
            return suggestions;
        }

        q = q.trim();

        List<User> users = userService.searchUsers(q, 0, 5);
        suggestions.put("users", users);

        return suggestions;
    }
}