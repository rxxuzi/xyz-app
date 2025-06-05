package com.rxxuzi.xyz.entity;

import java.time.LocalDateTime;

public class Post {
    private Long id;
    private Long userId;
    private String content;
    private String visibility;
    private Boolean isEdited;
    private Long parentPostId;
    private Long quotedPostId;
    private Integer replyCount;
    private Integer likeCount;
    private Integer quoteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Additional fields for display
    private User user;
    private Post parentPost;
    private Post quotedPost;
    private Boolean isLikedByCurrentUser;

    // Default constructor
    public Post() {
        this.visibility = "public";
        this.isEdited = false;
        this.replyCount = 0;
        this.likeCount = 0;
        this.quoteCount = 0;
    }

    // Constructor with content
    public Post(Long userId, String content) {
        this();
        this.userId = userId;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Boolean getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
    }

    public Long getParentPostId() {
        return parentPostId;
    }

    public void setParentPostId(Long parentPostId) {
        this.parentPostId = parentPostId;
    }

    public Long getQuotedPostId() {
        return quotedPostId;
    }

    public void setQuotedPostId(Long quotedPostId) {
        this.quotedPostId = quotedPostId;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getQuoteCount() {
        return quoteCount;
    }

    public void setQuoteCount(Integer quoteCount) {
        this.quoteCount = quoteCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getParentPost() {
        return parentPost;
    }

    public void setParentPost(Post parentPost) {
        this.parentPost = parentPost;
    }

    public Post getQuotedPost() {
        return quotedPost;
    }

    public void setQuotedPost(Post quotedPost) {
        this.quotedPost = quotedPost;
    }

    public Boolean getIsLikedByCurrentUser() {
        return isLikedByCurrentUser;
    }

    public void setIsLikedByCurrentUser(Boolean isLikedByCurrentUser) {
        this.isLikedByCurrentUser = isLikedByCurrentUser;
    }
}