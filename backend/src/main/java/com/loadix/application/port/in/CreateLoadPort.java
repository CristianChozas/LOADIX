package com.loadix.application.port.in;

import com.loadix.application.dto.request.CreateLoadRequest;
import com.loadix.application.dto.response.LoadResponse;

public interface CreateLoadPort {

    LoadResponse execute(String authenticatedEmail, CreateLoadRequest request);
}
