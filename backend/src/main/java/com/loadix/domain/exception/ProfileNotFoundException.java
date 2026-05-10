package com.loadix.domain.exception;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException() {
        super("Profile not found for authenticated user");
    }
}
