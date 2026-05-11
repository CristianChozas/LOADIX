package com.loadix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.loadix.application.port.in.CreateLoadPort;
import com.loadix.application.port.out.LoadPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.application.port.out.WarehouseProfilePort;
import com.loadix.application.usecase.load.CreateLoadUseCase;

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
}
