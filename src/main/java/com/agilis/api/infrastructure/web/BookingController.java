package com.agilis.api.infrastructure.web;

import com.agilis.api.application.booking.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final CreateBookingUseCase createBookingUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;
    private final ConfirmBookingUseCase confirmBookingUseCase;
    private final CompleteBookingUseCase completeBookingUseCase;
    private final GetBookingUseCase getBookingUseCase;

    public BookingController(
            CreateBookingUseCase createBookingUseCase,
            CancelBookingUseCase cancelBookingUseCase,
            ConfirmBookingUseCase confirmBookingUseCase,
            CompleteBookingUseCase completeBookingUseCase,
            GetBookingUseCase getBookingUseCase
    ) {
        this.createBookingUseCase  = createBookingUseCase;
        this.cancelBookingUseCase  = cancelBookingUseCase;
        this.confirmBookingUseCase = confirmBookingUseCase;
        this.completeBookingUseCase = completeBookingUseCase;
        this.getBookingUseCase     = getBookingUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateBookingUseCase.Output> create(
            @RequestBody CreateBookingUseCase.Input input
    ) {
        String clientId = getCurrentUserId();
        CreateBookingUseCase.Input inputWithClient = new CreateBookingUseCase.Input(
                clientId,
                input.serviceId(),
                input.scheduledAt()
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