package com.rxxuzi.xyz.controller;

import com.rxxuzi.xyz.entity.Message;
import com.rxxuzi.xyz.entity.User;
import com.rxxuzi.xyz.service.MessageService;
import com.rxxuzi.xyz.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String messages(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Get list of users with conversations
        List<User> conversationUsers = messageService.getConversationUsers(currentUser.getId());
        
        model.addAttribute("conversationUsers", conversationUsers);
        model.addAttribute("currentPage", "messages");
        
        return "messages";
    }

    @GetMapping("/conversation/{username}")
    public String conversation(@PathVariable String username, 
                              @RequestParam(defaultValue = "0") int page,
                              Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Get the other user
        User otherUser = userService.getUserByUsername(username);
        if (otherUser == null) {
            return "redirect:/messages";
        }

        // Get conversation messages
        List<Message> messages = messageService.getConversation(currentUser.getId(), otherUser.getId(), page, 20);
        
        // Mark messages as read
        messageService.markConversationAsRead(currentUser.getId(), otherUser.getId());
        
        // Get list of users with conversations for sidebar
        List<User> conversationUsers = messageService.getConversationUsers(currentUser.getId());
        
        model.addAttribute("messages", messages);
        model.addAttribute("otherUser", otherUser);
        model.addAttribute("conversationUsers", conversationUsers);
        model.addAttribute("page", page);
        model.addAttribute("currentPage", "messages");
        
        return "conversation";
    }

    @PostMapping("/send")
    @ResponseBody
    public String sendMessage(@RequestParam Long receiverId,
                             @RequestParam String content,
                             HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "{\"success\": false, \"message\": \"Not logged in\"}";
        }

        try {
            Message message = messageService.sendMessage(currentUser.getId(), receiverId, content);
            return "{\"success\": true, \"messageId\": " + message.getId() + "}";
        } catch (IllegalArgumentException e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"Failed to send message\"}";
        }
    }

    @PostMapping("/conversation/{username}/send")
    public String sendMessageToUser(@PathVariable String username,
                                   @RequestParam String content,
                                   HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        User receiver = userService.getUserByUsername(username);
        if (receiver == null) {
            return "redirect:/messages";
        }

        try {
            messageService.sendMessage(currentUser.getId(), receiver.getId(), content);
        } catch (Exception e) {
            // Handle error - could add flash attributes for error message
        }

        return "redirect:/messages/conversation/" + username;
    }

    @GetMapping("/new/{username}")
    public String startNewConversation(@PathVariable String username, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        User otherUser = userService.getUserByUsername(username);
        if (otherUser == null || otherUser.getId().equals(currentUser.getId())) {
            return "redirect:/messages";
        }

        return "redirect:/messages/conversation/" + username;
    }

    @GetMapping("/api/unread-count")
    @ResponseBody
    public String getUnreadCount(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "{\"count\": 0}";
        }

        int unreadCount = messageService.getTotalUnreadMessageCount(currentUser.getId());
        return "{\"count\": " + unreadCount + "}";
    }

    @PostMapping("/mark-read/{messageId}")
    @ResponseBody
    public String markAsRead(@PathVariable Long messageId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "{\"success\": false}";
        }

        boolean success = messageService.markMessageAsRead(messageId, currentUser.getId());
        return "{\"success\": " + success + "}";
    }
}