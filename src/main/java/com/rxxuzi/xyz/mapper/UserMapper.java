package com.rxxuzi.xyz.mapper;

import com.rxxuzi.xyz.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    // Create
    int insertUser(User user);

    // Read
    User selectUserById(@Param("id") Long id);
    User selectUserByUsername(@Param("username") String username);
    List<User> selectFollowers(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    List<User> selectFollowing(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);
    List<User> searchUsers(@Param("query") String query, @Param("offset") int offset, @Param("limit") int limit);
    List<User> selectRandomUsers(@Param("currentUserId") Long currentUserId, @Param("limit") int limit);

    // Update
    int updateUser(User user);
    int updateLastLoginAt(@Param("id") Long id);
    int incrementPostsCount(@Param("id") Long id);
    int decrementPostsCount(@Param("id") Long id);
    int incrementFollowersCount(@Param("id") Long id);
    int decrementFollowersCount(@Param("id") Long id);
    int incrementFollowingCount(@Param("id") Long id);
    int decrementFollowingCount(@Param("id") Long id);

    // Follow operations
    int insertFollower(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);
    int deleteFollower(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);
    boolean isFollowing(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);
}