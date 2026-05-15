package com.loadix.application.port.in;

import java.util.UUID;

import com.loadix.application.dto.response.LoadResponse;

public interface ReserveLoadPort {

    LoadResponse execute(String authenticatedEmail, UUID loadId);
}
