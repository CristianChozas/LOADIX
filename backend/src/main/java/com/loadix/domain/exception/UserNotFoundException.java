package com.loadix.domain.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Authenticated user not found");
    }
}
