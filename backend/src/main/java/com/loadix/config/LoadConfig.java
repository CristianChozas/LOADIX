package com.loadix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.loadix.application.port.in.CreateLoadPort;
import com.loadix.application.port.in.GetAvailableLoadsPort;
import com.loadix.application.port.in.GetCarrierDashboardMetricsPort;
import com.loadix.application.port.in.GetWarehouseDashboardMetricsPort;
import com.loadix.application.port.in.GetMyLoadsPort;
import com.loadix.application.port.in.UpdateMyLoadPort;
import com.loadix.application.port.in.UpdateMyLoadStatusPort;
import com.loadix.application.port.out.LoadPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.application.port.out.WarehouseProfilePort;
import com.loadix.application.usecase.load.CreateLoadUseCase;
import com.loadix.application.usecase.load.GetAvailableLoadsUseCase;
import com.loadix.application.usecase.load.GetCarrierDashboardMetricsUseCase;
import com.loadix.application.usecase.load.GetWarehouseDashboardMetricsUseCase;
import com.loadix.application.usecase.load.GetMyLoadsUseCase;
import com.loadix.application.usecase.load.UpdateMyLoadUseCase;
import com.loadix.application.usecase.load.UpdateMyLoadStatusUseCase;

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
    public GetWarehouseDashboardMetricsPort getWarehouseDashboardMetricsPort(
        UserAccountPort userAccountPort,
        LoadPort loadPort
    ) {
        return new GetWarehouseDashboardMetricsUseCase(userAccountPort, loadPort);
    }

    @Bean
    public GetCarrierDashboardMetricsPort getCarrierDashboardMetricsPort(
        UserAccountPort userAccountPort,
        LoadPort loadPort
    ) {
        return new GetCarrierDashboardMetricsUseCase(userAccountPort, loadPort);
    }

    @Bean
    public GetAvailableLoadsPort getAvailableLoadsPort(
        UserAccountPort userAccountPort,
        LoadPort loadPort
    ) {
        return new GetAvailableLoadsUseCase(userAccountPort, loadPort);
    }

    @Bean
    public UpdateMyLoadPort updateMyLoadPort(
        UserAccountPort userAccountPort,
        LoadPort loadPort
    ) {
        return new UpdateMyLoadUseCase(userAccountPort, loadPort);
    }

    @Bean
    public UpdateMyLoadStatusPort updateMyLoadStatusPort(
        UserAccountPort userAccountPort,
        LoadPort loadPort
    ) {
        return new UpdateMyLoadStatusUseCase(userAccountPort, loadPort);
    }
}
