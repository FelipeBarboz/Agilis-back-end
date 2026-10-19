package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public class GetStoreMonthlyCalendarUseCase {

    private final BookingRepository bookingRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public GetStoreMonthlyCalendarUseCase(
            BookingRepository bookingRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.bookingRepository         = bookingRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());

        storeMembershipRepository.findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalStateException("No permission to view this store’s bookings."));

        YearMonth ym = YearMonth.of(input.year(), input.month());
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate   = ym.atEndOfMonth();

        List<LocalDate> dates = bookingRepository.findDistinctDatesByStoreAndDateRange(storeId, startDate, endDate);
        List<Integer> days = dates.stream().map(LocalDate::getDayOfMonth).sorted().toList();

        return new Output(
                input.year(),
                input.month(),
                dates,
                days
        );
    }

    @Schema(name = "GetStoreMonthlyCalendarInput")
    public record Input(String requesterId, String storeId, int year, int month) {}

    @Schema(name = "GetStoreMonthlyCalendarOutput")
    public record Output(
            int year,
            int month,
            List<LocalDate> datesWithBookings,
            List<Integer> daysWithBookings
    ) {}
}
