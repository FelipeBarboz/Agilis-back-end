package com.agilis.api.infrastructure.web;

import com.agilis.api.application.message.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Mensagens", description = "Envio e consulta de mensagens de um agendamento")
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
    @Operation(summary = "Envia uma mensagem no chat do agendamento")
    public ResponseEntity<SendMessageUseCase.Output> send(
            @RequestBody SendMessageUseCase.Input input
    ) {
        return ResponseEntity.ok(sendMessageUseCase.execute(input));
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Lista todas as mensagens do chat de um agendamento")
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