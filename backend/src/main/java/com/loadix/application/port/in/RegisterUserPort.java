package com.loadix.application.port.in;

import com.loadix.application.dto.request.RegisterRequest;
import com.loadix.application.dto.response.AuthUserResponse;

public interface RegisterUserPort {

    AuthUserResponse execute(RegisterRequest request);
}
