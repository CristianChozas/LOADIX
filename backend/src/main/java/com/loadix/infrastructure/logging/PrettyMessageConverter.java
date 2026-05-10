package com.loadix.infrastructure.logging;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class PrettyMessageConverter extends ClassicConverter {

    private static final Pattern HTTP_LOG_PATTERN = Pattern.compile("^(HTTP\\s+\\S+\\s+\\S+\\s+)(\\d{3})(\\s+\\S+)$");
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";

    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        Matcher matcher = HTTP_LOG_PATTERN.matcher(message);

        if (!matcher.matches()) {
            return message;
        }

        String status = matcher.group(2);
        return matcher.group(1) + colorFor(status) + status + ANSI_RESET + matcher.group(3);
    }

    private String colorFor(String status) {
        int statusCode = Integer.parseInt(status);

        if (statusCode >= 500) {
            return ANSI_RED;
        }
        if (statusCode >= 400) {
            return ANSI_YELLOW;
        }
        if (statusCode >= 300) {
            return ANSI_CYAN;
        }
        return ANSI_GREEN;
    }
}
