package com.agilis.api.application.support;

import com.agilis.api.domain.support.EmailSender;
import com.agilis.api.domain.support.SupportMessage;
import com.agilis.api.domain.support.SupportMessageRepository;
import com.agilis.api.domain.support.SupportSubject;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class CreateSupportMessageUseCase {

    private final SupportMessageRepository supportMessageRepository;
    private final EmailSender emailSender;
    private final String supportEmail;

    public CreateSupportMessageUseCase(SupportMessageRepository supportMessageRepository, EmailSender emailSender, String supportEmail) {
        this.supportMessageRepository = supportMessageRepository;
        this.emailSender              = emailSender;
        this.supportEmail             = supportEmail;
    }

    public Output execute(Input input) {
        UUID userId = input.userId() != null ? UUID.fromString(input.userId()) : null;

        SupportMessage msg = SupportMessage.create(
                userId, input.name(), input.email(), input.subject(), input.message()
        );
        supportMessageRepository.save(msg);

        String emailBody = """
            Nova mensagem de suporte recebida:

            Nome: %s
            Email: %s
            Assunto: %s

            Mensagem:
            %s
            """.formatted(msg.getName(), msg.getEmail(), msg.getSubject(), msg.getMessage());

        // nao deixa uma falha de email quebrar o cadastro da mensagem — loga e segue
        try {
            emailSender.send(supportEmail, "[Agilis Suporte] " + msg.getSubject(), emailBody);
        } catch (Exception e) {
            System.out.println("Falha ao enviar email de suporte: " + e.getMessage());
        }

        return new Output(msg.getId().toString());
    }

    @Schema(name = "CreateSupportMessageInput")
    public record Input(String userId, String name, String email, SupportSubject subject, String message) {}

    @Schema(name = "CreateSupportMessageOutput")
    public record Output(String messageId) {}
}