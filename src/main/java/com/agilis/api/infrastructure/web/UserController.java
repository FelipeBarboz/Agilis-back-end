package com.agilis.api.infrastructure.web;

import com.agilis.api.application.user.RegisterClientUseCase;
import com.agilis.api.application.provider.RegisterProviderUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final RegisterClientUseCase registerClientUseCase;
    private final RegisterProviderUseCase registerProviderUseCase;

    public UserController(
            RegisterClientUseCase registerClientUseCase,
            RegisterProviderUseCase registerProviderUseCase
    ) {
        this.registerClientUseCase  = registerClientUseCase;
        this.registerProviderUseCase = registerProviderUseCase;
    }

    // chamado dps que o Supabase Auth criar o usuário
    @PostMapping("/auth/register/client")
    public ResponseEntity<RegisterClientUseCase.Output> registerClient(
            @RequestBody RegisterClientUseCase.Input input
    ) {
        return ResponseEntity.ok(registerClientUseCase.execute(input));
    }

    // usuario ja logado abre uma loja
    @PostMapping("/auth/register/provider")
    public ResponseEntity<RegisterProviderUseCase.Output> registerProvider(
            @RequestBody RegisterProviderUseCase.Input input
    ) {
        String userId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

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
}