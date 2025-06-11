package com.rxxuzi.xyz.service;

import com.rxxuzi.xyz.entity.Message;
import com.rxxuzi.xyz.entity.User;
import com.rxxuzi.xyz.mapper.MessageMapper;
import com.rxxuzi.xyz.security.InputValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private InputValidator inputValidator;

    @Transactional
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        // Validate input
        if (senderId == null || receiverId == null) {
            throw new IllegalArgumentException("Sender and receiver IDs are required");
        }
        
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Cannot send message to yourself");
        }

        if (!inputValidator.isValidMessageContent(content)) {
            throw new IllegalArgumentException("Invalid message content");
        }

        // Create and save message
        Message message = new Message(senderId, receiverId, content);
        messageMapper.insertMessage(message);
        
        return message;
    }

    public List<Message> getConversation(Long userId1, Long userId2, int page, int size) {
        if (userId1 == null || userId2 == null) {
            return Collections.emptyList();
        }
        
        int offset = page * size;
        List<Message> messages = messageMapper.selectConversation(userId1, userId2, offset, size);
        
        // Reverse the list to show oldest first (since we order by DESC for pagination)
        Collections.reverse(messages);
        return messages;
    }

    public List<User> getConversationUsers(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        
        return messageMapper.selectConversationUsers(userId);
    }

    public Message getLatestMessage(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) {
            return null;
        }
        
        return messageMapper.selectLatestMessageBetweenUsers(userId1, userId2);
    }

    public int getUnreadMessageCount(Long userId, Long fromUserId) {
        if (userId == null || fromUserId == null) {
            return 0;
        }
        
        return messageMapper.countUnreadMessages(userId, fromUserId);
    }

    public int getTotalUnreadMessageCount(Long userId) {
        if (userId == null) {
            return 0;
        }
        
        return messageMapper.countTotalUnreadMessages(userId);
    }

    @Transactional
    public boolean markMessageAsRead(Long messageId, Long userId) {
        if (messageId == null || userId == null) {
            return false;
        }
        
        return messageMapper.markMessageAsRead(messageId) > 0;
    }

    @Transactional
    public boolean markConversationAsRead(Long receiverId, Long senderId) {
        if (receiverId == null || senderId == null) {
            return false;
        }
        
        return messageMapper.markConversationAsRead(receiverId, senderId) > 0;
    }

    @Transactional
    public boolean deleteMessage(Long messageId, Long userId) {
        if (messageId == null || userId == null) {
            return false;
        }
        
        return messageMapper.deleteMessage(messageId, userId) > 0;
    }

    public boolean canSendMessage(Long senderId, Long receiverId) {
        // Basic validation - can be extended with blocking/privacy logic
        return senderId != null && receiverId != null && !senderId.equals(receiverId);
    }
}