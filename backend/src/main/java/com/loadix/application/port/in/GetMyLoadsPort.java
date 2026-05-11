package com.loadix.application.port.in;

import java.time.LocalDate;

import com.loadix.application.dto.response.MyLoadsPageResponse;

public interface GetMyLoadsPort {

    MyLoadsPageResponse execute(
        String authenticatedEmail,
        int page,
        int size,
        boolean sortAsc,
        LocalDate pickupDateFrom,
        LocalDate pickupDateTo
    );
}
