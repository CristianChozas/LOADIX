package com.loadix.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super("Email already exists");
    }
}
