package com.loadix.application.usecase.load;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.loadix.application.dto.response.WarehouseDashboardResponse;
import com.loadix.application.port.in.GetWarehouseDashboardMetricsPort;
import com.loadix.application.port.out.LoadPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.valueobject.LoadStatus;
import com.loadix.domain.valueobject.UserRole;

public class GetWarehouseDashboardMetricsUseCase implements GetWarehouseDashboardMetricsPort {

    private static final Map<DayOfWeek, String> WEEKDAY_LABELS = Map.of(
        DayOfWeek.MONDAY, "L",
        DayOfWeek.TUESDAY, "M",
        DayOfWeek.WEDNESDAY, "X",
        DayOfWeek.THURSDAY, "J",
        DayOfWeek.FRIDAY, "V",
        DayOfWeek.SATURDAY, "S",
        DayOfWeek.SUNDAY, "D"
    );

    private final UserAccountPort userAccountPort;
    private final LoadPort loadPort;

    public GetWarehouseDashboardMetricsUseCase(UserAccountPort userAccountPort, LoadPort loadPort) {
        this.userAccountPort = userAccountPort;
        this.loadPort = loadPort;
    }

    @Override
    public WarehouseDashboardResponse execute(String authenticatedEmail) {
        var user = userAccountPort.findByEmail(normalizeEmail(authenticatedEmail))
            .orElseThrow(UserNotFoundException::new);

        if (user.role() != UserRole.WAREHOUSE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only warehouse users can view dashboard metrics");
        }

        Instant now = Instant.now();
        Instant monthStart = LocalDate.now(ZoneOffset.UTC)
            .withDayOfMonth(1)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC);

        long monthlyLoads = loadPort.countCreatedByWarehouseUserIdBetween(user.id(), monthStart, now.plusSeconds(1));

        var currentDate = LocalDate.now(ZoneOffset.UTC);
        LocalDate weekStartDate = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEndDateExclusive = weekStartDate.plusDays(7);

        Instant weekStart = weekStartDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant weekEndExclusive = weekEndDateExclusive.atStartOfDay().toInstant(ZoneOffset.UTC);

        Map<DayOfWeek, Long> weeklyCountByDay = new EnumMap<>(DayOfWeek.class);
        Arrays.stream(DayOfWeek.values()).forEach(day -> weeklyCountByDay.put(day, 0L));

        loadPort.findCreatedByWarehouseUserIdBetween(user.id(), weekStart, weekEndExclusive)
            .forEach(load -> {
                DayOfWeek day = load.createdAt().atOffset(ZoneOffset.UTC).getDayOfWeek();
                weeklyCountByDay.computeIfPresent(day, (k, current) -> current + 1L);
            });

        List<WarehouseDashboardResponse.WeeklyActivityPoint> weeklyActivity = List.of(
            buildWeeklyPoint(DayOfWeek.MONDAY, weeklyCountByDay),
            buildWeeklyPoint(DayOfWeek.TUESDAY, weeklyCountByDay),
            buildWeeklyPoint(DayOfWeek.WEDNESDAY, weeklyCountByDay),
            buildWeeklyPoint(DayOfWeek.THURSDAY, weeklyCountByDay),
            buildWeeklyPoint(DayOfWeek.FRIDAY, weeklyCountByDay),
            buildWeeklyPoint(DayOfWeek.SATURDAY, weeklyCountByDay),
            buildWeeklyPoint(DayOfWeek.SUNDAY, weeklyCountByDay)
        );

        Map<LoadStatus, Long> statusCounts = new EnumMap<>(LoadStatus.class);
        Arrays.stream(LoadStatus.values()).forEach(status -> statusCounts.put(status, 0L));

        loadPort.countByWarehouseUserIdGroupedByStatus(user.id())
            .forEach(item -> statusCounts.put(item.status(), item.count()));

        List<WarehouseDashboardResponse.StatusDistributionItem> statusDistribution = Arrays.stream(LoadStatus.values())
            .map(status -> new WarehouseDashboardResponse.StatusDistributionItem(status, statusCounts.get(status)))
            .toList();

        return new WarehouseDashboardResponse(monthlyLoads, weeklyActivity, statusDistribution);
    }

    private WarehouseDashboardResponse.WeeklyActivityPoint buildWeeklyPoint(
        DayOfWeek day,
        Map<DayOfWeek, Long> weeklyCountByDay
    ) {
        return new WarehouseDashboardResponse.WeeklyActivityPoint(
            WEEKDAY_LABELS.get(day),
            weeklyCountByDay.getOrDefault(day, 0L)
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
