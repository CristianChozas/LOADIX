package com.loadix.application.port.in;

import com.loadix.application.dto.response.WarehouseDashboardResponse;

public interface GetWarehouseDashboardMetricsPort {

    WarehouseDashboardResponse execute(String authenticatedEmail);
}
