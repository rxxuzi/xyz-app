package com.rxxuzi.xyz.mapper;

import com.rxxuzi.xyz.entity.Message;
import com.rxxuzi.xyz.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {

    // Create
    int insertMessage(Message message);

    // Read
    List<Message> selectConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2,
                                     @Param("offset") int offset, @Param("limit") int limit);
    
    List<User> selectConversationUsers(@Param("userId") Long userId);
    
    Message selectLatestMessageBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    int countUnreadMessages(@Param("userId") Long userId, @Param("fromUserId") Long fromUserId);
    
    int countTotalUnreadMessages(@Param("userId") Long userId);

    // Update
    int markMessageAsRead(@Param("messageId") Long messageId);
    
    int markConversationAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);

    // Delete
    int deleteMessage(@Param("messageId") Long messageId, @Param("userId") Long userId);
}