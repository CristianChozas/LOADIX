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

import com.loadix.application.dto.response.CarrierDashboardResponse;
import com.loadix.application.port.in.GetCarrierDashboardMetricsPort;
import com.loadix.application.port.out.LoadPort;
import com.loadix.application.port.out.UserAccountPort;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.domain.valueobject.CargoType;
import com.loadix.domain.valueobject.LoadStatus;
import com.loadix.domain.valueobject.UserRole;

public class GetCarrierDashboardMetricsUseCase implements GetCarrierDashboardMetricsPort {

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

    public GetCarrierDashboardMetricsUseCase(UserAccountPort userAccountPort, LoadPort loadPort) {
        this.userAccountPort = userAccountPort;
        this.loadPort = loadPort;
    }

    @Override
    public CarrierDashboardResponse execute(String authenticatedEmail) {
        var user = userAccountPort.findByEmail(normalizeEmail(authenticatedEmail))
            .orElseThrow(UserNotFoundException::new);

        if (user.role() != UserRole.CARRIER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only carrier users can view dashboard metrics");
        }

        long availableLoads = loadPort.countByStatus(LoadStatus.PUBLISHED);

        LocalDate currentDate = LocalDate.now(ZoneOffset.UTC);
        LocalDate weekStartDate = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEndDateExclusive = weekStartDate.plusDays(7);

        Instant weekStart = weekStartDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant weekEndExclusive = weekEndDateExclusive.atStartOfDay().toInstant(ZoneOffset.UTC);

        Map<DayOfWeek, Long> weeklyCountByDay = new EnumMap<>(DayOfWeek.class);
        Arrays.stream(DayOfWeek.values()).forEach(day -> weeklyCountByDay.put(day, 0L));

        loadPort.findByStatusAndCreatedAtBetween(LoadStatus.PUBLISHED, weekStart, weekEndExclusive)
            .forEach(load -> {
                DayOfWeek day = load.createdAt().atOffset(ZoneOffset.UTC).getDayOfWeek();
                weeklyCountByDay.computeIfPresent(day, (key, current) -> current + 1L);
            });

        List<CarrierDashboardResponse.WeeklyActivityPoint> weeklyActivity = List.of(
            buildWeeklyPoint(DayOfWeek.MONDAY, weeklyCountByDay),
            buildWeeklyPoint(DayOfWeek.TUESDAY, weeklyCountByDay),
            buildWeeklyPoint(DayOfWeek.WEDNESDAY, weeklyCountByDay),
            buildWeeklyPoint(DayOfWeek.THURSDAY, weeklyCountByDay),
            buildWeeklyPoint(DayOfWeek.FRIDAY, weeklyCountByDay),
            buildWeeklyPoint(DayOfWeek.SATURDAY, weeklyCountByDay),
            buildWeeklyPoint(DayOfWeek.SUNDAY, weeklyCountByDay)
        );

        Map<CargoType, Long> cargoTypeCounts = new EnumMap<>(CargoType.class);
        Arrays.stream(CargoType.values()).forEach(cargoType -> cargoTypeCounts.put(cargoType, 0L));

        loadPort.countByStatusGroupedByCargoType(LoadStatus.PUBLISHED)
            .forEach(item -> cargoTypeCounts.put(item.cargoType(), item.count()));

        List<CarrierDashboardResponse.CargoTypeDistributionItem> cargoTypeDistribution = Arrays.stream(CargoType.values())
            .map(cargoType -> new CarrierDashboardResponse.CargoTypeDistributionItem(cargoType, cargoTypeCounts.get(cargoType)))
            .toList();

        return new CarrierDashboardResponse(availableLoads, weeklyActivity, cargoTypeDistribution);
    }

    private CarrierDashboardResponse.WeeklyActivityPoint buildWeeklyPoint(
        DayOfWeek day,
        Map<DayOfWeek, Long> weeklyCountByDay
    ) {
        return new CarrierDashboardResponse.WeeklyActivityPoint(
            WEEKDAY_LABELS.get(day),
            weeklyCountByDay.getOrDefault(day, 0L)
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
