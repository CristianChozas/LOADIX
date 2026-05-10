package com.loadix.application.port.in;

import com.loadix.application.dto.response.CarrierProfileResponse;

public interface GetCarrierProfilePort {

    CarrierProfileResponse execute(String authenticatedEmail);

}
