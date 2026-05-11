package com.loadix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.loadix.application.port.in.CreateLoadPort;
import com.loadix.application.port.in.GetMyLoadsPort;
import com.loadix.application.port.in.UpdateMyLoadPort;
import com.loadix.application.port.out.LoadPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.application.port.out.WarehouseProfilePort;
import com.loadix.application.usecase.load.CreateLoadUseCase;
import com.loadix.application.usecase.load.GetMyLoadsUseCase;
import com.loadix.application.usecase.load.UpdateMyLoadUseCase;

@Configuration
public class LoadConfig {

    @Bean
    public CreateLoadPort createLoadPort(
        UserAccountPort userAccountPort,
        WarehouseProfilePort warehouseProfilePort,
        LoadPort loadPort
    ) {
        return new CreateLoadUseCase(userAccountPort, warehouseProfilePort, loadPort);
    }

    @Bean
    public GetMyLoadsPort getMyLoadsPort(
        UserAccountPort userAccountPort,
        LoadPort loadPort
    ) {
        return new GetMyLoadsUseCase(userAccountPort, loadPort);
    }

    @Bean
    public UpdateMyLoadPort updateMyLoadPort(
        UserAccountPort userAccountPort,
        LoadPort loadPort
    ) {
        return new UpdateMyLoadUseCase(userAccountPort, loadPort);
    }
}
