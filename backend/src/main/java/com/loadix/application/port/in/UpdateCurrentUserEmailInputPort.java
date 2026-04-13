package com.loadix.application.port.in;

import com.loadix.application.dto.request.UpdateEmailRequest;
import com.loadix.application.dto.response.AuthSessionResponse;

public interface UpdateCurrentUserEmailInputPort {

    AuthSessionResponse execute(String currentEmail, UpdateEmailRequest request);
}
