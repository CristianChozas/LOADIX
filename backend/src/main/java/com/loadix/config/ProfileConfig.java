package com.loadix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.loadix.application.port.in.CreateCarrierProfilePort;
import com.loadix.application.port.in.CreateWarehouseProfilePort;
import com.loadix.application.port.in.GetWarehouseProfilePort;
import com.loadix.application.port.in.UpdateWarehouseProfilePort;
import com.loadix.application.port.out.CarrierProfilePort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.application.port.out.WarehouseProfilePort;
import com.loadix.application.usecase.profile.CreateCarrierProfileUseCase;
import com.loadix.application.usecase.profile.CreateWarehouseProfileUseCase;
import com.loadix.application.usecase.profile.GetWarehouseProfileUseCase;
import com.loadix.application.usecase.profile.UpdateWarehouseProfileUseCase;

@Configuration
public class ProfileConfig {
    @Bean
    public CreateWarehouseProfilePort createWarehouseProfilePort(
        UserAccountPort userAccountPort,
        WarehouseProfilePort warehouseProfilePort
    ) {
        return new CreateWarehouseProfileUseCase(userAccountPort, warehouseProfilePort);
    }

    @Bean 
    public GetWarehouseProfilePort getWarehouseProfilePort(
        UserAccountPort userAccountPort,
        WarehouseProfilePort warehouseProfilePort
    ) {
        return new GetWarehouseProfileUseCase(userAccountPort, warehouseProfilePort);
    }

    @Bean
    public UpdateWarehouseProfilePort updateWarehouseProfilePort(
        UserAccountPort userAccountPort,
        WarehouseProfilePort warehouseProfilePort
    ) {
        return new UpdateWarehouseProfileUseCase(userAccountPort, warehouseProfilePort);
    }

    @Bean
    public CreateCarrierProfilePort createCarrierProfilePort(
        UserAccountPort userAccountPort,
        CarrierProfilePort carrierProfilePort
    ){
        return new CreateCarrierProfileUseCase(userAccountPort, carrierProfilePort);
    }
}
