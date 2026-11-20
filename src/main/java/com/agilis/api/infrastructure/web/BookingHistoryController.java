package com.agilis.api.infrastructure.web;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.agilis.api.application.booking.GetBookingDetailUseCase;
import com.agilis.api.application.booking.GetBookingHistoryUseCase;
import com.agilis.api.application.booking.ReportBookingIssueUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Histórico de Agendamentos", description = "Histórico e detalhes de agendamentos do usuário")
@RestController
@RequestMapping("/api/v1/bookings")
public class BookingHistoryController {

    private final GetBookingHistoryUseCase getBookingHistoryUseCase;
    private final GetBookingDetailUseCase getBookingDetailUseCase;
    private final ReportBookingIssueUseCase reportBookingIssueUseCase;

    public BookingHistoryController(GetBookingHistoryUseCase getBookingHistoryUseCase, GetBookingDetailUseCase getBookingDetailUseCase, ReportBookingIssueUseCase reportBookingIssueUseCase) {
        this.getBookingHistoryUseCase = getBookingHistoryUseCase;
        this.getBookingDetailUseCase  = getBookingDetailUseCase;
        this.reportBookingIssueUseCase = reportBookingIssueUseCase;
    }

    @GetMapping("/history")
    @Operation(summary = "Consulta o histórico de agendamentos do cliente")
    public ResponseEntity<GetBookingHistoryUseCase.Output> history(
            @RequestParam(defaultValue = "CLIENT") GetBookingHistoryUseCase.Role role,
            @RequestParam(required = false) GetBookingHistoryUseCase.DisplayStatus status
    ) {
        String requesterId = getCurrentUserId();
        return ResponseEntity.ok(getBookingHistoryUseCase.execute(
                new GetBookingHistoryUseCase.Input(requesterId, role, status)
        ));
    }

    @GetMapping("/{bookingId}/detail")
    @Operation(summary = "Consulta os detalhes de um agendamento no histórico")
    public ResponseEntity<GetBookingDetailUseCase.Output> detail(@PathVariable String bookingId) {
        String requesterId = getCurrentUserId();
        return ResponseEntity.ok(getBookingDetailUseCase.execute(requesterId, bookingId));
    }

    @PostMapping("/{bookingId}/report-issue")
    @Operation(summary = "Reporta um problema ou reclamação sobre um agendamento")
    public ResponseEntity<Void> reportIssue(
            @PathVariable String bookingId,
            @RequestBody ReportIssueRequest request
    ) {
        String requesterId = getCurrentUserId();
        reportBookingIssueUseCase.execute(new ReportBookingIssueUseCase.Input(requesterId, bookingId, request.description()));
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public record ReportIssueRequest(String description) {}
}