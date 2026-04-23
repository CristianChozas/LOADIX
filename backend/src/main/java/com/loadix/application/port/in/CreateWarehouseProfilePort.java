package com.loadix.application.port.in;

import com.loadix.application.dto.request.CreateWarehouseProfileRequest;
import com.loadix.application.dto.response.WarehouseProfileResponse;

public interface CreateWarehouseProfilePort {
    
    WarehouseProfileResponse execute(String authenticatedEmail, CreateWarehouseProfileRequest request);

}
