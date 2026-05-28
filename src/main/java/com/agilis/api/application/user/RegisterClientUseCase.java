package com.agilis.api.application.user;

import com.agilis.api.domain.client.Client;
import com.agilis.api.domain.client.ClientRepository;
import com.agilis.api.domain.user.User;
import com.agilis.api.domain.user.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegisterClientUseCase {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public RegisterClientUseCase(UserRepository userRepository, ClientRepository clientRepository) {
        this.userRepository  = userRepository;
        this.clientRepository = clientRepository;
    }

    public Output execute(Input input, String userId) {
        if (userRepository.existsByEmail(input.email())) {
            throw new IllegalArgumentException("Email already registered.");
        }
        if (clientRepository.existsByCpf(input.cpf())) {
            throw new IllegalArgumentException("CPF already registered.");
        }

        User user = User.reconstitute(
                UUID.fromString(userId),
                input.name(),
                input.email(),
                input.phone(),
                LocalDateTime.now()
        );
        userRepository.save(user);

        Client client = Client.create(user.getId(), input.cpf());
        clientRepository.save(client);

        return new Output(user.getId().toString(), user.getName(), user.getEmail());
    }

    @Schema(name = "RegisterClientInput")
    public record Input(String name, String email, String phone, String cpf) {}

    @Schema(name = "RegisterClientOutput")
    public record Output(String userId, String name, String email) {}
}