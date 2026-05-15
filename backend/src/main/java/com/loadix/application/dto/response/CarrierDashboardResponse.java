package com.loadix.application.dto.response;

import java.util.List;

import com.loadix.domain.valueobject.CargoType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CarrierDashboardResponse", description = "Carrier dashboard metrics payload")
public record CarrierDashboardResponse(
    @Schema(description = "Total loads reserved by the carrier")
    long reservedLoads,
    @Schema(description = "Total loads currently available in the marketplace")
    long availableLoads,
    @Schema(description = "Current week marketplace activity split by weekday")
    List<WeeklyActivityPoint> weeklyActivity,
    @Schema(description = "Available load distribution grouped by cargo type")
    List<CargoTypeDistributionItem> cargoTypeDistribution
) {
    public record WeeklyActivityPoint(
        @Schema(description = "Weekday short label", example = "L")
        String day,
        @Schema(description = "Loads published on that weekday", example = "3")
        long count
    ) {
    }

    public record CargoTypeDistributionItem(
        @Schema(description = "Cargo type")
        CargoType cargoType,
        @Schema(description = "Number of available loads for this cargo type", example = "8")
        long count
    ) {
    }
}
