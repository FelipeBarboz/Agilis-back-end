package com.agilis.api.infrastructure.web;

import com.agilis.api.application.booking.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final CreateBookingUseCase createBookingUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;
    private final ConfirmBookingUseCase confirmBookingUseCase;
    private final CompleteBookingUseCase completeBookingUseCase;
    private final GetStoreBookingsUseCase getStoreBookingsUseCase;
    private final GetBookingUseCase getBookingUseCase;
    private final GetStoreDailyBookingsOverviewUseCase getStoreDailyBookingsOverviewUseCase;
    private final GetStoreMonthlyCalendarUseCase getStoreMonthlyCalendarUseCase;

    public BookingController(
            CreateBookingUseCase createBookingUseCase,
            CancelBookingUseCase cancelBookingUseCase,
            ConfirmBookingUseCase confirmBookingUseCase,
            CompleteBookingUseCase completeBookingUseCase,
            GetStoreBookingsUseCase getStoreBookingsUseCase,
            GetBookingUseCase getBookingUseCase,
            GetStoreDailyBookingsOverviewUseCase getStoreDailyBookingsOverviewUseCase,
            GetStoreMonthlyCalendarUseCase getStoreMonthlyCalendarUseCase
    ) {
        this.createBookingUseCase  = createBookingUseCase;
        this.cancelBookingUseCase  = cancelBookingUseCase;
        this.confirmBookingUseCase = confirmBookingUseCase;
        this.completeBookingUseCase = completeBookingUseCase;
        this.getStoreBookingsUseCase = getStoreBookingsUseCase;
        this.getBookingUseCase     = getBookingUseCase;
        this.getStoreDailyBookingsOverviewUseCase = getStoreDailyBookingsOverviewUseCase;
        this.getStoreMonthlyCalendarUseCase = getStoreMonthlyCalendarUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateBookingUseCase.Output> create(
            @RequestBody CreateBookingUseCase.Input input
    ) {
        String clientId = getCurrentUserId();
        CreateBookingUseCase.Input inputWithClient = new CreateBookingUseCase.Input(
                clientId,
                input.serviceId(),
                input.employeeId(),
                input.scheduledAt(),
                input.notes(),
                input.paymentMethod(),
                input.couponCode()
        );
        return ResponseEntity.ok(createBookingUseCase.execute(inputWithClient));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetBookingUseCase.Output> getById(@PathVariable String id) {
        return ResponseEntity.ok(getBookingUseCase.execute(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<GetBookingUseCase.Output>> getMyBookings() {
        String clientId = getCurrentUserId();
        return ResponseEntity.ok(getBookingUseCase.executeByClient(clientId));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<GetBookingUseCase.Output>> getByService(
            @PathVariable String serviceId
    ) {
        return ResponseEntity.ok(getBookingUseCase.executeByService(serviceId));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<GetStoreBookingsUseCase.Output>> getStoreBookings(
            @PathVariable String storeId,
            @RequestParam(required = false) LocalDate date
    ) {
        String requesterId = getCurrentUserId();
        return ResponseEntity.ok(getStoreBookingsUseCase.execute(
                new GetStoreBookingsUseCase.Input(requesterId, storeId, date)
        ));
    }

    @GetMapping("/store/{storeId}/overview")
    public ResponseEntity<GetStoreDailyBookingsOverviewUseCase.Output> getStoreDailyOverview(
            @PathVariable String storeId,
            @RequestParam(required = false) LocalDate date
    ) {
        String requesterId = getCurrentUserId();
        return ResponseEntity.ok(getStoreDailyBookingsOverviewUseCase.execute(
                new GetStoreDailyBookingsOverviewUseCase.Input(requesterId, storeId, date)
        ));
    }

    @GetMapping("/store/{storeId}/calendar")
    public ResponseEntity<GetStoreMonthlyCalendarUseCase.Output> getStoreMonthlyCalendar(
            @PathVariable String storeId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        String requesterId = getCurrentUserId();
        return ResponseEntity.ok(getStoreMonthlyCalendarUseCase.execute(
                new GetStoreMonthlyCalendarUseCase.Input(requesterId, storeId, year, month)
        ));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable String id) {
        String requesterId = getCurrentUserId();
        cancelBookingUseCase.execute(new CancelBookingUseCase.Input(id, requesterId));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable String id) {
        String requesterId = getCurrentUserId();
        confirmBookingUseCase.execute(new ConfirmBookingUseCase.Input(id, requesterId));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable String id) {
        String requesterId = getCurrentUserId();
        completeBookingUseCase.execute(new CompleteBookingUseCase.Input(id, requesterId));
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}