package com.loadix.application.port.in;

import com.loadix.application.dto.response.CarrierDashboardResponse;

public interface GetCarrierDashboardMetricsPort {

    CarrierDashboardResponse execute(String authenticatedEmail);
}
