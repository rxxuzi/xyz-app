package com.rxxuzi.xyz.mapper;

import com.rxxuzi.xyz.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    // Create
    int insertPost(Post post);

    // Read
    Post selectPostById(@Param("id") Long id);
    Post selectPostWithDetails(@Param("id") Long id, @Param("currentUserId") Long currentUserId);
    List<Post> selectPostsByUserId(@Param("userId") Long userId, @Param("currentUserId") Long currentUserId,
                                   @Param("offset") int offset, @Param("limit") int limit);
    List<Post> selectUserPostsOnly(@Param("userId") Long userId, @Param("currentUserId") Long currentUserId,
                                   @Param("offset") int offset, @Param("limit") int limit);
    List<Post> selectUserLikedPosts(@Param("userId") Long userId, @Param("currentUserId") Long currentUserId,
                                    @Param("offset") int offset, @Param("limit") int limit);
    List<Post> selectTimeline(@Param("userId") Long userId, @Param("currentUserId") Long currentUserId,
                              @Param("offset") int offset, @Param("limit") int limit);
    List<Post> selectPublicTimeline(@Param("currentUserId") Long currentUserId,
                                    @Param("offset") int offset, @Param("limit") int limit);
    List<Post> selectFollowingTimeline(@Param("userId") Long userId, @Param("currentUserId") Long currentUserId,
                                       @Param("offset") int offset, @Param("limit") int limit);
    List<Post> selectReplies(@Param("parentPostId") Long parentPostId, @Param("currentUserId") Long currentUserId,
                             @Param("offset") int offset, @Param("limit") int limit);
    List<Post> searchPosts(@Param("query") String query, @Param("currentUserId") Long currentUserId,
                           @Param("offset") int offset, @Param("limit") int limit);
    List<Post> selectPostsByHashtag(@Param("hashtag") String hashtag, @Param("currentUserId") Long currentUserId,
                                    @Param("offset") int offset, @Param("limit") int limit);
    List<Post> selectReplyChain(@Param("postId") Long postId, @Param("currentUserId") Long currentUserId);

    // Update
    int updatePost(Post post);
    int incrementReplyCount(@Param("id") Long id);
    int decrementReplyCount(@Param("id") Long id);
    int incrementLikeCount(@Param("id") Long id);
    int decrementLikeCount(@Param("id") Long id);
    int incrementQuoteCount(@Param("id") Long id);
    int decrementQuoteCount(@Param("id") Long id);
    int softDeletePost(@Param("id") Long id);

    // Like operations
    int insertLike(@Param("postId") Long postId, @Param("userId") Long userId);
    int deleteLike(@Param("postId") Long postId, @Param("userId") Long userId);
    boolean isLiked(@Param("postId") Long postId, @Param("userId") Long userId);

    // Hashtag operations
    int insertHashtag(@Param("tag") String tag);
    Long selectHashtagId(@Param("tag") String tag);
    int insertPostHashtag(@Param("postId") Long postId, @Param("hashtagId") Long hashtagId);
}