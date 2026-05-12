package com.loadix.application.dto.response;

import java.util.List;

import com.loadix.domain.valueobject.LoadStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "WarehouseDashboardResponse", description = "Warehouse dashboard metrics payload")
public record WarehouseDashboardResponse(
    @Schema(description = "Total loads created by the warehouse in the current month")
    long monthlyLoads,
    @Schema(description = "Current week activity split by weekday")
    List<WeeklyActivityPoint> weeklyActivity,
    @Schema(description = "Load distribution grouped by status")
    List<StatusDistributionItem> statusDistribution
) {
    public record WeeklyActivityPoint(
        @Schema(description = "Weekday short label", example = "L")
        String day,
        @Schema(description = "Loads created on that weekday", example = "3")
        long count
    ) {
    }

    public record StatusDistributionItem(
        @Schema(description = "Load status")
        LoadStatus status,
        @Schema(description = "Number of loads in this status", example = "8")
        long count
    ) {
    }
}
