package com.loadix.application.port.in;

import com.loadix.application.dto.response.MyLoadsPageResponse;

public interface GetAvailableLoadsPort {

    MyLoadsPageResponse execute(
        String authenticatedEmail,
        int page,
        int size,
        boolean sortAsc
    );
}
