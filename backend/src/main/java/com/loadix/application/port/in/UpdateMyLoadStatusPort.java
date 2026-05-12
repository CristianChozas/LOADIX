package com.loadix.application.port.in;

import java.util.UUID;

import com.loadix.application.dto.request.UpdateLoadStatusRequest;
import com.loadix.application.dto.response.LoadResponse;

public interface UpdateMyLoadStatusPort {

    LoadResponse execute(String authenticatedEmail, UUID loadId, UpdateLoadStatusRequest request);
}
