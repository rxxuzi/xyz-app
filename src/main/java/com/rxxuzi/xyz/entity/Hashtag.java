package com.rxxuzi.xyz.entity;

import java.time.LocalDateTime;

public class Hashtag {
    private Long id;
    private String tag;
    private LocalDateTime createdAt;
    
    // Transient field for trending hashtags with post count
    private transient Long postCount;

    // Default constructor
    public Hashtag() {}

    // Constructor with tag
    public Hashtag(String tag) {
        this.tag = tag;
    }

    // Constructor with tag and post count (for trending)
    public Hashtag(String tag, Long postCount) {
        this.tag = tag;
        this.postCount = postCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getPostCount() {
        return postCount;
    }

    public void setPostCount(Long postCount) {
        this.postCount = postCount;
    }
}