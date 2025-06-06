package com.rxxuzi.xyz.controller;

import com.rxxuzi.xyz.entity.Post;
import com.rxxuzi.xyz.entity.User;
import com.rxxuzi.xyz.service.PostService;
import com.rxxuzi.xyz.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/home";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        User user = userService.login(username, password);
        if (user == null) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }

        session.setAttribute("user", user);
        return "redirect:/home";
    }

    @GetMapping("/register")
    public String registerForm(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/home";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        if (username.length() < 3 || username.length() > 30) {
            model.addAttribute("error", "Username must be between 3 and 30 characters");
            return "register";
        }

        try {
            userService.register(username, password);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }

    @GetMapping("/u/{username}")
    public String userProfile(@PathVariable String username,
                              Model model,
                              HttpSession session,
                              @RequestParam(defaultValue = "0") int page) {
        logger.info("Accessing profile: {}", username);

        User profileUser = userService.getUserByUsername(username);
        if (profileUser == null) {
            logger.warn("User not found: {}", username);
            return "404";
        }

        User currentUser = (User) session.getAttribute("user");
        Long currentUserId = currentUser != null ? currentUser.getId() : null;

        logger.info("Profile user: {} (ID: {})", profileUser.getUsername(), profileUser.getId());
        if (currentUser != null) {
            logger.info("Current user: {} (ID: {})", currentUser.getUsername(), currentUser.getId());
        }

        List<Post> posts = postService.getUserPosts(profileUser.getId(), currentUserId, page, 20);

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("posts", posts != null ? posts : Collections.emptyList());
        model.addAttribute("page", page);

        boolean isFollowing = false;
        if (currentUser != null && !currentUser.getId().equals(profileUser.getId())) {
            isFollowing = userService.isFollowing(currentUser.getId(), profileUser.getId());
            logger.info("Is following: {}", isFollowing);
        }
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("currentUserId", currentUserId);

        return "profile";
    }

    @GetMapping("/u/{username}/followers")
    public String userFollowers(@PathVariable String username,
                                Model model,
                                HttpSession session,
                                @RequestParam(defaultValue = "0") int page) {
        try {
            User profileUser = userService.getUserByUsername(username);
            if (profileUser == null) {
                return "404";
            }

            User currentUser = (User) session.getAttribute("user");

            List<User> followers = userService.getFollowers(profileUser.getId(), page, 20);
            if (followers == null) {
                followers = Collections.emptyList();
            }

            model.addAttribute("profileUser", profileUser);
            model.addAttribute("users", followers);
            model.addAttribute("page", page);
            model.addAttribute("type", "followers");
            model.addAttribute("currentUserId", currentUser != null ? currentUser.getId() : null);

            logger.info("Followers for {}: {} users found", username, followers.size());

            return "follow-list";
        } catch (Exception e) {
            logger.error("Error loading followers for user: " + username, e);
            model.addAttribute("error", "Error loading followers");
            return "error";
        }
    }

    @GetMapping("/u/{username}/following")
    public String userFollowing(@PathVariable String username,
                                Model model,
                                HttpSession session,
                                @RequestParam(defaultValue = "0") int page) {
        try {
            User profileUser = userService.getUserByUsername(username);
            if (profileUser == null) {
                return "404";
            }

            User currentUser = (User) session.getAttribute("user");

            List<User> following = userService.getFollowing(profileUser.getId(), page, 20);
            if (following == null) {
                following = Collections.emptyList();
            }

            model.addAttribute("profileUser", profileUser);
            model.addAttribute("users", following);
            model.addAttribute("page", page);
            model.addAttribute("type", "following");
            model.addAttribute("currentUserId", currentUser != null ? currentUser.getId() : null);

            logger.info("Following for {}: {} users found", username, following.size());

            return "follow-list";
        } catch (Exception e) {
            logger.error("Error loading following for user: " + username, e);
            model.addAttribute("error", "Error loading following");
            return "error";
        }
    }

    @PostMapping("/u/{username}/follow")
    @ResponseBody
    public String followUser(@PathVariable String username, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return "{\"success\": false, \"message\": \"Not logged in\"}";
            }

            User targetUser = userService.getUserByUsername(username);
            if (targetUser == null) {
                return "{\"success\": false, \"message\": \"User not found\"}";
            }

            boolean success = userService.follow(currentUser.getId(), targetUser.getId());
            return "{\"success\": " + success + "}";
        } catch (Exception e) {
            logger.error("Error following user: " + username, e);
            return "{\"success\": false, \"message\": \"Error occurred\"}";
        }
    }

    @PostMapping("/u/{username}/unfollow")
    @ResponseBody
    public String unfollowUser(@PathVariable String username, HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return "{\"success\": false, \"message\": \"Not logged in\"}";
            }

            User targetUser = userService.getUserByUsername(username);
            if (targetUser == null) {
                return "{\"success\": false, \"message\": \"User not found\"}";
            }

            boolean success = userService.unfollow(currentUser.getId(), targetUser.getId());
            return "{\"success\": " + success + "}";
        } catch (Exception e) {
            logger.error("Error unfollowing user: " + username, e);
            return "{\"success\": false, \"message\": \"Error occurred\"}";
        }
    }
}