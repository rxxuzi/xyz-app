package com.rxxuzi.xyz.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(HttpServletRequest request, Model model) {
        logger.warn("404 error for URL: {}", request.getRequestURL());
        model.addAttribute("message", "Page not found");
        return "404";
    }

    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Exception ex, HttpServletRequest request, Model model) {
        logger.error("Unexpected error occurred at URL: {}", request.getRequestURL(), ex);
        model.addAttribute("error", "An unexpected error occurred");
        return "error";
    }
}