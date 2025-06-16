package com.rxxuzi.xyz.service;

import com.rxxuzi.xyz.entity.User;
import com.rxxuzi.xyz.mapper.UserMapper;
import com.rxxuzi.xyz.security.InputValidator;
import com.rxxuzi.xyz.security.XssSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InputValidator inputValidator;

    @Autowired
    private XssSanitizer xssSanitizer;

    @Transactional
    public User register(String username, String password) {
        if (!inputValidator.isValidUsername(username)) {
            throw new RuntimeException("Invalid username format");
        }

        if (!inputValidator.isValidPassword(password)) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        if (userMapper.selectUserByUsername(username) != null) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User(username, hashPassword(password));
        userMapper.insertUser(user);
        return user;
    }

    public User login(String username, String password) {
        if (!inputValidator.isValidUsername(username)) {
            return null;
        }

        User user = userMapper.selectUserByUsername(username);
        if (user == null || !user.getPasswordHash().equals(hashPassword(password))) {
            return null;
        }

        if (!"active".equals(user.getStatus())) {
            throw new RuntimeException("Account is not active");
        }

        userMapper.updateLastLoginAt(user.getId());
        return user;
    }

    public User getUserById(Long id) {
        return userMapper.selectUserById(id);
    }

    public User getUserByUsername(String username) {
        if (!inputValidator.isValidUsername(username)) {
            return null;
        }
        return userMapper.selectUserByUsername(username);
    }

    @Transactional
    public boolean follow(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new RuntimeException("Cannot follow yourself");
        }

        if (userMapper.isFollowing(followerId, followeeId)) {
            return false;
        }

        userMapper.insertFollower(followerId, followeeId);
        userMapper.incrementFollowingCount(followerId);
        userMapper.incrementFollowersCount(followeeId);
        return true;
    }

    @Transactional
    public boolean unfollow(Long followerId, Long followeeId) {
        if (!userMapper.isFollowing(followerId, followeeId)) {
            return false;
        }

        userMapper.deleteFollower(followerId, followeeId);
        userMapper.decrementFollowingCount(followerId);
        userMapper.decrementFollowersCount(followeeId);
        return true;
    }

    public boolean isFollowing(Long followerId, Long followeeId) {
        return userMapper.isFollowing(followerId, followeeId);
    }

    public List<User> getFollowers(Long userId, int page, int size) {
        int offset = page * size;
        return userMapper.selectFollowers(userId, offset, size);
    }

    public List<User> getFollowing(Long userId, int page, int size) {
        int offset = page * size;
        return userMapper.selectFollowing(userId, offset, size);
    }

    public List<User> searchUsers(String query, int page, int size) {
        int offset = page * size;
        String sanitizedQuery = xssSanitizer.sanitize(query);
        return userMapper.searchUsers(sanitizedQuery, offset, size);
    }

    public List<User> getRandomUsers(Long currentUserId, int limit) {
        return userMapper.selectRandomUsers(currentUserId, limit);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 60);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
}