package com.rxxuzi.xyz.controller;

import com.rxxuzi.xyz.entity.Hashtag;
import com.rxxuzi.xyz.entity.User;
import com.rxxuzi.xyz.service.PostService;
import com.rxxuzi.xyz.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sidebar")
public class SidebarController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    /**
     * Get sidebar data including trending hashtags and user recommendations
     */
    @GetMapping("/data")
    public Map<String, Object> getSidebarData(HttpSession session) {
        Map<String, Object> sidebarData = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("user");
        Long currentUserId = currentUser != null ? currentUser.getId() : null;

        // Get trending hashtags (limit to 5)
        List<Hashtag> trendingHashtags = postService.getTrendingHashtags(5);
        sidebarData.put("trendingHashtags", trendingHashtags);

        // Get user recommendations (limit to 2)
        List<User> userRecommendations = userService.getRandomUsers(currentUserId, 2);
        
        // Set follow status for each recommended user if current user is logged in
        if (currentUser != null && userRecommendations != null) {
            for (User user : userRecommendations) {
                boolean isFollowing = userService.isFollowing(currentUser.getId(), user.getId());
                user.setIsFollowing(isFollowing);
            }
        }
        
        sidebarData.put("userRecommendations", userRecommendations);
        sidebarData.put("isLoggedIn", currentUser != null);

        return sidebarData;
    }

    /**
     * Get only trending hashtags
     */
    @GetMapping("/trending")
    public Map<String, Object> getTrendingHashtags() {
        Map<String, Object> response = new HashMap<>();
        List<Hashtag> trendingHashtags = postService.getTrendingHashtags(10);
        response.put("trendingHashtags", trendingHashtags);
        return response;
    }

    /**
     * Get only user recommendations
     */
    @GetMapping("/recommendations")
    public Map<String, Object> getUserRecommendations(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("user");
        Long currentUserId = currentUser != null ? currentUser.getId() : null;

        List<User> userRecommendations = userService.getRandomUsers(currentUserId, 3);
        
        // Set follow status for each recommended user if current user is logged in
        if (currentUser != null && userRecommendations != null) {
            for (User user : userRecommendations) {
                boolean isFollowing = userService.isFollowing(currentUser.getId(), user.getId());
                user.setIsFollowing(isFollowing);
            }
        }
        
        response.put("userRecommendations", userRecommendations);
        response.put("isLoggedIn", currentUser != null);
        
        return response;
    }
}