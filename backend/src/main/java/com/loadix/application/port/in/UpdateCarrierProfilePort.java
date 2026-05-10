package com.loadix.application.port.in;

import com.loadix.application.dto.request.UpdateCarrierProfileRequest;
import com.loadix.application.dto.response.CarrierProfileResponse;

public interface UpdateCarrierProfilePort {

    CarrierProfileResponse execute(String authenticatedEmail, UpdateCarrierProfileRequest request);

}
