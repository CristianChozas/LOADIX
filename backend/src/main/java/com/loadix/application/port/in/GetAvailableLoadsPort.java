package com.loadix.application.port.in;

import com.loadix.application.dto.response.MyLoadsPageResponse;
import com.loadix.domain.model.AvailableLoadsFilters;

public interface GetAvailableLoadsPort {

    MyLoadsPageResponse execute(
        String authenticatedEmail,
        int page,
        int size,
        boolean sortAsc,
        AvailableLoadsFilters filters
    );
}
