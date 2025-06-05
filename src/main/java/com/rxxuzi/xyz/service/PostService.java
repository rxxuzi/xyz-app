package com.rxxuzi.xyz.service;

import com.rxxuzi.xyz.entity.Post;
import com.rxxuzi.xyz.mapper.PostMapper;
import com.rxxuzi.xyz.mapper.UserMapper;
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

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#(\\w+)");

    @Transactional
    public Post createPost(Long userId, String content, Long parentPostId, Long quotedPostId) {
        Post post = new Post(userId, content);
        post.setParentPostId(parentPostId);
        post.setQuotedPostId(quotedPostId);

        postMapper.insertPost(post);

        // Process hashtags
        processHashtags(post.getId(), content);

        // Update counters
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

    public List<Post> getTimeline(Long userId, int page, int size) {
        int offset = page * size;
        // For Youタブでは全ての公開投稿を表示
        return postMapper.selectPublicTimeline(userId, offset, size);
    }

    public List<Post> getPublicTimeline(Long currentUserId, int page, int size) {
        int offset = page * size;
        return postMapper.selectPublicTimeline(currentUserId, offset, size);
    }

    public List<Post> getReplies(Long parentPostId, Long currentUserId, int page, int size) {
        int offset = page * size;
        return postMapper.selectReplies(parentPostId, currentUserId, offset, size);
    }

    public List<Post> searchPosts(String query, Long currentUserId, int page, int size) {
        int offset = page * size;
        return postMapper.searchPosts(query, currentUserId, offset, size);
    }

    public List<Post> getPostsByHashtag(String hashtag, Long currentUserId, int page, int size) {
        int offset = page * size;
        // Remove # if present
        if (hashtag.startsWith("#")) {
            hashtag = hashtag.substring(1);
        }
        return postMapper.selectPostsByHashtag(hashtag.toLowerCase(), currentUserId, offset, size);
    }

    private void processHashtags(Long postId, String content) {
        Matcher matcher = HASHTAG_PATTERN.matcher(content);
        while (matcher.find()) {
            String tag = matcher.group(1).toLowerCase();

            // Insert hashtag if not exists
            Long hashtagId = postMapper.selectHashtagId(tag);
            if (hashtagId == null) {
                postMapper.insertHashtag(tag);
                hashtagId = postMapper.selectHashtagId(tag);
            }

            // Link post to hashtag
            postMapper.insertPostHashtag(postId, hashtagId);
        }
    }

    public String processContent(String content) {
        if (content == null) return "";

        // Convert hashtags to links
        String processed = content.replaceAll(
                "#(\\w+)",
                "<a href=\"/search?q=%23$1\" class=\"hashtag\">#$1</a>"
        );

        // Convert @mentions to links (optional feature)
        processed = processed.replaceAll(
                "@(\\w+)",
                "<a href=\"/u/$1\" class=\"mention\">@$1</a>"
        );

        return processed;
    }

    public String highlightSearchTerm(String content, String searchTerm) {
        if (content == null || searchTerm == null || searchTerm.isEmpty()) {
            return content;
        }

        // First process the content (hashtags, mentions)
        String processed = processContent(content);

        // Then highlight search terms
        String escapedTerm = searchTerm.replaceAll("([\\[\\](){}.+*?^$\\\\|])", "\\\\$1");
        return processed.replaceAll(
                "(?i)(" + escapedTerm + ")",
                "<span class=\"highlight\">$1</span>"
        );
    }
}