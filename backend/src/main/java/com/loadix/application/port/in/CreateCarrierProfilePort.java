package com.loadix.application.port.in;

import com.loadix.application.dto.request.CreateCarrierProfileRequest;
import com.loadix.application.dto.response.CarrierProfileResponse;

public interface CreateCarrierProfilePort {

    CarrierProfileResponse execute(String authenticatedEmail, CreateCarrierProfileRequest request);

}
