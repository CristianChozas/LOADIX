package com.loadix.application.port.in;

import com.loadix.application.dto.request.LoginRequest;
import com.loadix.application.dto.response.AuthSessionResponse;

public interface LoginUserInputPort {

    AuthSessionResponse execute(LoginRequest request);
}
