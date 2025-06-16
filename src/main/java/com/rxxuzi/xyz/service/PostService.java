package com.rxxuzi.xyz.service;

import com.rxxuzi.xyz.entity.Post;
import com.rxxuzi.xyz.entity.Hashtag;
import com.rxxuzi.xyz.mapper.PostMapper;
import com.rxxuzi.xyz.mapper.UserMapper;
import com.rxxuzi.xyz.security.XssSanitizer;
import com.rxxuzi.xyz.security.InputValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private XssSanitizer xssSanitizer;

    @Autowired
    private InputValidator inputValidator;

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#(\\w+)");
    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");

    @Transactional
    public Post createPost(Long userId, String content, Long parentPostId, Long quotedPostId) {
        String cleanedContent = inputValidator.trimAndClean(content);

        if (!inputValidator.isValidPostContent(cleanedContent)) {
            throw new IllegalArgumentException("Invalid post content");
        }

        String sanitizedContent = xssSanitizer.sanitize(cleanedContent);

        Post post = new Post(userId, sanitizedContent);
        post.setParentPostId(parentPostId);
        post.setQuotedPostId(quotedPostId);

        postMapper.insertPost(post);

        processHashtags(post.getId(), sanitizedContent);

        userMapper.incrementPostsCount(userId);

        if (parentPostId != null) {
            postMapper.incrementReplyCount(parentPostId);
        }

        if (quotedPostId != null) {
            postMapper.incrementQuoteCount(quotedPostId);
        }

        return post;
    }

    @Transactional
    public boolean deletePost(Long postId, Long userId) {
        Post post = postMapper.selectPostById(postId);
        if (post == null || !post.getUserId().equals(userId)) {
            return false;
        }

        postMapper.softDeletePost(postId);
        userMapper.decrementPostsCount(userId);

        if (post.getParentPostId() != null) {
            postMapper.decrementReplyCount(post.getParentPostId());
        }

        if (post.getQuotedPostId() != null) {
            postMapper.decrementQuoteCount(post.getQuotedPostId());
        }

        return true;
    }

    @Transactional
    public boolean likePost(Long postId, Long userId) {
        if (postMapper.isLiked(postId, userId)) {
            return false;
        }

        postMapper.insertLike(postId, userId);
        postMapper.incrementLikeCount(postId);
        return true;
    }

    @Transactional
    public boolean unlikePost(Long postId, Long userId) {
        if (!postMapper.isLiked(postId, userId)) {
            return false;
        }

        postMapper.deleteLike(postId, userId);
        postMapper.decrementLikeCount(postId);
        return true;
    }

    public Post getPost(Long postId, Long currentUserId) {
        return postMapper.selectPostWithDetails(postId, currentUserId);
    }

    public List<Post> getUserPosts(Long userId, Long currentUserId, int page, int size) {
        int offset = page * size;
        return postMapper.selectPostsByUserId(userId, currentUserId, offset, size);
    }

    public List<Post> getUserPostsOnly(Long userId, Long currentUserId, int page, int size) {
        int offset = page * size;
        return postMapper.selectUserPostsOnly(userId, currentUserId, offset, size);
    }

    public List<Post> getUserPostsAndReplies(Long userId, Long currentUserId, int page, int size) {
        int offset = page * size;
        return postMapper.selectPostsByUserId(userId, currentUserId, offset, size);
    }

    public List<Post> getUserLikedPosts(Long userId, Long currentUserId, int page, int size) {
        int offset = page * size;
        return postMapper.selectUserLikedPosts(userId, currentUserId, offset, size);
    }

    public List<Post> getTimeline(Long userId, int page, int size) {
        int offset = page * size;
        return postMapper.selectPublicTimeline(userId, offset, size);
    }

    public List<Post> getPublicTimeline(Long currentUserId, int page, int size) {
        int offset = page * size;
        return postMapper.selectPublicTimeline(currentUserId, offset, size);
    }

    public List<Post> getFollowingTimeline(Long userId, Long currentUserId, int page, int size) {
        int offset = page * size;
        return postMapper.selectFollowingTimeline(userId, currentUserId, offset, size);
    }

    public List<Post> getReplies(Long parentPostId, Long currentUserId, int page, int size) {
        int offset = page * size;
        return postMapper.selectReplies(parentPostId, currentUserId, offset, size);
    }

    public List<Post> getReplyChain(Long postId, Long currentUserId) {
        return postMapper.selectReplyChain(postId, currentUserId);
    }

    public List<Post> searchPosts(String query, Long currentUserId, int page, int size) {
        int offset = page * size;
        String sanitizedQuery = xssSanitizer.sanitize(query);
        return postMapper.searchPosts(sanitizedQuery, currentUserId, offset, size);
    }

    public List<Post> getPostsByHashtag(String hashtag, Long currentUserId, int page, int size) {
        int offset = page * size;
        if (hashtag.startsWith("#")) {
            hashtag = hashtag.substring(1);
        }
        String sanitizedHashtag = xssSanitizer.sanitize(hashtag);
        return postMapper.selectPostsByHashtag(sanitizedHashtag.toLowerCase(), currentUserId, offset, size);
    }

    private void processHashtags(Long postId, String content) {
        Matcher matcher = HASHTAG_PATTERN.matcher(content);
        while (matcher.find()) {
            String tag = matcher.group(1).toLowerCase();

            Long hashtagId = postMapper.selectHashtagId(tag);
            if (hashtagId == null) {
                postMapper.insertHashtag(tag);
                hashtagId = postMapper.selectHashtagId(tag);
            }

            postMapper.insertPostHashtag(postId, hashtagId);
        }
    }

    public String processContent(String content) {
        if (content == null) return "";

        String processed = content;

        processed = processed.replaceAll(
                "#(\\w+)",
                "<a href=\"/search?q=%23$1\" class=\"hashtag\">#$1</a>"
        );

        processed = processed.replaceAll(
                "@(\\w+)",
                "<a href=\"/u/$1\" class=\"mention\">@$1</a>"
        );

        processed = processed.replaceAll("\r\n", "\n");
        processed = processed.replaceAll("\r", "\n");
        processed = processed.replaceAll("\n", "<br>");

        return processed;
    }

    public String highlightSearchTerm(String content, String searchTerm) {
        if (content == null || searchTerm == null || searchTerm.isEmpty()) {
            return processContent(content);
        }

        String escapedTerm = searchTerm.replaceAll("([\\[\\](){}.+*?^$\\\\|])", "\\\\$1");

        String highlighted = content.replaceAll(
                "(?i)(?![^<>]*>)(" + escapedTerm + ")",
                "<span class=\"highlight\">$1</span>"
        );

        return processContentWithHighlight(highlighted);
    }

    private String processContentWithHighlight(String content) {
        if (content == null) return "";

        String placeholder = "§§§HIGHLIGHT§§§";
        String endPlaceholder = "§§§/HIGHLIGHT§§§";

        content = content.replaceAll("<span class=\"highlight\">", placeholder);
        content = content.replaceAll("</span>", endPlaceholder);

        content = content.replaceAll(
                "#(\\w+)",
                "<a href=\"/search?q=%23$1\" class=\"hashtag\">#$1</a>"
        );

        content = content.replaceAll(
                "@(\\w+)",
                "<a href=\"/u/$1\" class=\"mention\">@$1</a>"
        );

        content = content.replaceAll("\r\n", "\n");
        content = content.replaceAll("\r", "\n");
        content = content.replaceAll("\n", "<br>");

        content = content.replaceAll(placeholder, "<span class=\"highlight\">");
        content = content.replaceAll(endPlaceholder, "</span>");

        return content;
    }

    public List<Hashtag> getTrendingHashtags(int limit) {
        return postMapper.selectTrendingHashtags(limit);
    }
}