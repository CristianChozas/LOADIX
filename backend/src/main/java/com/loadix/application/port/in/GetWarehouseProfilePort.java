package com.loadix.application.port.in;

import com.loadix.application.dto.response.WarehouseProfileResponse;

public interface GetWarehouseProfilePort {

    WarehouseProfileResponse execute(String authenticatedEmail);

}
