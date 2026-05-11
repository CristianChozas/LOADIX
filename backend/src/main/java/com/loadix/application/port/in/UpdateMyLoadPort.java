package com.loadix.application.port.in;

import java.util.UUID;

import com.loadix.application.dto.request.UpdateLoadRequest;
import com.loadix.application.dto.response.LoadResponse;

public interface UpdateMyLoadPort {

    LoadResponse execute(String authenticatedEmail, UUID loadId, UpdateLoadRequest request);
}
