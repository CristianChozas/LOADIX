package com.loadix.application.port.in;

import com.loadix.application.dto.request.UpdateWarehouseProfileRequest;
import com.loadix.application.dto.response.WarehouseProfileResponse;

public interface UpdateWarehouseProfilePort {
    
    WarehouseProfileResponse execute(String authenticatedEmail, UpdateWarehouseProfileRequest updateWarehouseProfileRequest);

}
