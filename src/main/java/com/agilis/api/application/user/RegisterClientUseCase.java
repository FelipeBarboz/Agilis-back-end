package com.agilis.api.application.user;

import com.agilis.api.domain.client.Client;
import com.agilis.api.domain.client.ClientRepository;
import com.agilis.api.domain.user.User;
import com.agilis.api.domain.user.UserRepository;

public class RegisterClientUseCase {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public RegisterClientUseCase(UserRepository userRepository, ClientRepository clientRepository) {
        this.userRepository  = userRepository;
        this.clientRepository = clientRepository;
    }

    public Output execute(Input input) {
        if (userRepository.existsByEmail(input.email())) {
            throw new IllegalArgumentException("Email already registered.");
        }
        if (clientRepository.existsByCpf(input.cpf())) {
            throw new IllegalArgumentException("CPF already registered.");
        }

        User user = User.create(input.name(), input.email(), input.phone());
        userRepository.save(user);

        Client client = Client.create(user.getId(), input.cpf());
        clientRepository.save(client);

        return new Output(user.getId().toString(), user.getName(), user.getEmail());
    }

    public record Input(String name, String email, String phone, String cpf) {}

    public record Output(String userId, String name, String email) {}
}