package com.loadix.application.mapper;

import com.loadix.application.dto.response.AuthUserResponse;
import com.loadix.domain.model.UserAccount;

public class AuthMapper {

    public AuthUserResponse toAuthUserResponse(UserAccount user) {
        return new AuthUserResponse(
                user.id(),
                user.email(),
                user.role(),
                user.profileCompleted()
        );
    }
}
