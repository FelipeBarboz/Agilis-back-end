package com.agilis.api.application.user;

import com.agilis.api.domain.user.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class UpdateUserUseCase {

    private final UserRepository userRepository;

    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Output execute(Input input) {
        var user = userRepository.findById(UUID.fromString(input.userId()))
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.changeName(input.name());
        user.changePhone(input.phone());

        userRepository.save(user);

        return new Output(
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }

    @Schema(name = "UpdateUserInput")
    public record Input(String userId, String name, String phone) {}

    @Schema(name = "UpdateUserOutput")
    public record Output(String userId, String name, String email, String phone) {}
}