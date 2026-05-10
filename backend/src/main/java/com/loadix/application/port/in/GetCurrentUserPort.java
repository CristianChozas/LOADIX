package com.loadix.application.port.in;

import com.loadix.application.dto.response.AuthUserResponse;

public interface GetCurrentUserPort {

    AuthUserResponse execute(String email);
}
