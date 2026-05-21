package com.agilis.api.infrastructure.web;

import com.agilis.api.application.message.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final SendMessageUseCase sendMessageUseCase;
    private final GetMessagesUseCase getMessagesUseCase;

    public MessageController(
            SendMessageUseCase sendMessageUseCase,
            GetMessagesUseCase getMessagesUseCase
    ) {
        this.sendMessageUseCase  = sendMessageUseCase;
        this.getMessagesUseCase  = getMessagesUseCase;
    }

    @PostMapping
    public ResponseEntity<SendMessageUseCase.Output> send(
            @RequestBody SendMessageUseCase.Input input
    ) {
        return ResponseEntity.ok(sendMessageUseCase.execute(input));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<GetMessagesUseCase.Output>> getByBooking(
            @PathVariable String bookingId
    ) {
        String requesterId = getCurrentUserId();
        return ResponseEntity.ok(getMessagesUseCase.execute(
                new GetMessagesUseCase.Input(bookingId, requesterId)
        ));
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}