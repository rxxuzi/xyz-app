package com.rxxuzi.xyz.security;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class XssSanitizer {

    private static final Map<String, String> HTML_ESCAPE_CHARS = new HashMap<>();

    static {
        HTML_ESCAPE_CHARS.put("&", "&amp;");
        HTML_ESCAPE_CHARS.put("<", "&lt;");
        HTML_ESCAPE_CHARS.put(">", "&gt;");
        HTML_ESCAPE_CHARS.put("\"", "&quot;");
        HTML_ESCAPE_CHARS.put("'", "&#39;");
    }

    private static final Pattern SCRIPT_PATTERN = Pattern.compile(
            "(?i)<script[^>]*>.*?</script>",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL
    );

    private static final Pattern DANGEROUS_EVENT_PATTERN = Pattern.compile(
            "(?i)on\\w+\\s*=",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern DANGEROUS_PROTOCOLS = Pattern.compile(
            "(?i)(javascript|data|vbscript|file|about):",
            Pattern.CASE_INSENSITIVE
    );

    public String sanitize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String sanitized = input;

        sanitized = SCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = DANGEROUS_EVENT_PATTERN.matcher(sanitized).replaceAll("");
        sanitized = DANGEROUS_PROTOCOLS.matcher(sanitized).replaceAll("");

        StringBuilder escaped = new StringBuilder();
        for (char c : sanitized.toCharArray()) {
            String escapeChar = HTML_ESCAPE_CHARS.get(String.valueOf(c));
            if (escapeChar != null) {
                escaped.append(escapeChar);
            } else {
                escaped.append(c);
            }
        }

        return escaped.toString();
    }

    public boolean containsDangerousContent(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        return SCRIPT_PATTERN.matcher(input).find() ||
                DANGEROUS_EVENT_PATTERN.matcher(input).find() ||
                DANGEROUS_PROTOCOLS.matcher(input).find();
    }

    public String escapeHtml(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder escaped = new StringBuilder();
        for (char c : input.toCharArray()) {
            String escapeChar = HTML_ESCAPE_CHARS.get(String.valueOf(c));
            if (escapeChar != null) {
                escaped.append(escapeChar);
            } else {
                escaped.append(c);
            }
        }

        return escaped.toString();
    }
}