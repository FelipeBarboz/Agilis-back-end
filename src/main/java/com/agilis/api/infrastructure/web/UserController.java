package com.agilis.api.infrastructure.web;

import com.agilis.api.application.user.RegisterClientUseCase;
import com.agilis.api.application.user.UpdateUserUseCase;
import com.agilis.api.domain.user.User;
import com.agilis.api.domain.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Usuários", description = "Cadastro e gerenciamento de perfil do usuário")
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final RegisterClientUseCase registerClientUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UserRepository userRepository;

    public UserController(
            RegisterClientUseCase registerClientUseCase,
            UpdateUserUseCase updateUserUseCase,
            UserRepository userRepository
    ) {
        this.registerClientUseCase   = registerClientUseCase;
        this.updateUserUseCase       = updateUserUseCase;
        this.userRepository          = userRepository;
    }

    @PostMapping("/auth/register/client")
    @Operation(summary = "Cadastra o perfil de cliente após criação no Supabase")
    public ResponseEntity<RegisterClientUseCase.Output> registerClient(
            @RequestBody RegisterClientUseCase.Input input
    ) {
        String userId = getCurrentUserId();
        return ResponseEntity.ok(registerClientUseCase.execute(input, userId));
    }

    @GetMapping("/users/me")
    @Operation(summary = "Retorna os dados do perfil do usuário autenticado")
    public ResponseEntity<User> getMe() {
        String userId = getCurrentUserId();
        return userRepository.findById(UUID.fromString(userId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/me")
    @Operation(summary = "Atualiza os dados do perfil do usuário autenticado")
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