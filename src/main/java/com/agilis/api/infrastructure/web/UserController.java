package com.agilis.api.infrastructure.web;

import com.agilis.api.application.user.RegisterClientUseCase;
import com.agilis.api.application.user.UpdateUserUseCase;
import com.agilis.api.application.provider.RegisterProviderUseCase;
import com.agilis.api.domain.user.User;
import com.agilis.api.domain.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final RegisterClientUseCase registerClientUseCase;
    private final RegisterProviderUseCase registerProviderUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UserRepository userRepository;

    public UserController(
            RegisterClientUseCase registerClientUseCase,
            RegisterProviderUseCase registerProviderUseCase,
            UpdateUserUseCase updateUserUseCase,
            UserRepository userRepository
    ) {
        this.registerClientUseCase   = registerClientUseCase;
        this.registerProviderUseCase = registerProviderUseCase;
        this.updateUserUseCase       = updateUserUseCase;
        this.userRepository          = userRepository;
    }

    @PostMapping("/auth/register/client")
    public ResponseEntity<RegisterClientUseCase.Output> registerClient(
            @RequestBody RegisterClientUseCase.Input input
    ) {
        String userId = getCurrentUserId();
        return ResponseEntity.ok(registerClientUseCase.execute(input, userId));
    }

    @PostMapping("/auth/register/provider")
    public ResponseEntity<RegisterProviderUseCase.Output> registerProvider(
            @RequestBody RegisterProviderUseCase.Input input
    ) {
        String userId = getCurrentUserId();
        RegisterProviderUseCase.Input inputWithUser = new RegisterProviderUseCase.Input(
                userId,
                input.cnpj(),
                input.storeName(),
                input.slug(),
                input.description(),
                input.profileImgUrl()
        );
        return ResponseEntity.ok(registerProviderUseCase.execute(inputWithUser));
    }

    @GetMapping("/users/me")
    public ResponseEntity<User> getMe() {
        String userId = getCurrentUserId();
        return userRepository.findById(UUID.fromString(userId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/me")
    public ResponseEntity<UpdateUserUseCase.Output> updateMe(
            @RequestBody UpdateUserUseCase.Input input
    ) {
        String userId = getCurrentUserId();
        UpdateUserUseCase.Input inputWithUser = new UpdateUserUseCase.Input(
                userId,
                input.name(),
                input.phone(),
                input.email()
        );
        return ResponseEntity.ok(updateUserUseCase.execute(inputWithUser));
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}