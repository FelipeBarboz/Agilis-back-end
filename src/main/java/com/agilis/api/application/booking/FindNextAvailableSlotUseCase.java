package com.agilis.api.application.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class FindNextAvailableSlotUseCase {

    private static final int MAX_DAYS_AHEAD = 14;

    private final GetAvailableSlotsUseCase getAvailableSlotsUseCase;

    public FindNextAvailableSlotUseCase(GetAvailableSlotsUseCase getAvailableSlotsUseCase) {
        this.getAvailableSlotsUseCase = getAvailableSlotsUseCase;
    }

    public Optional<LocalDateTime> execute(String serviceId) {
        LocalDate date = LocalDate.now();

        for (int i = 0; i < MAX_DAYS_AHEAD; i++) {
            GetAvailableSlotsUseCase.Output result = getAvailableSlotsUseCase.execute(
                    new GetAvailableSlotsUseCase.Input(serviceId, date, null)
            );

            List<LocalTime> slots = result.availableSlots();
            if (!slots.isEmpty()) {
                return Optional.of(LocalDateTime.of(date, slots.get(0)));
            }
            date = date.plusDays(1);
        }
        return Optional.empty();
    }
}