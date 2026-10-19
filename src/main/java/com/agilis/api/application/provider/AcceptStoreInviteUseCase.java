package com.agilis.api.application.provider;

import com.agilis.api.domain.client.ClientRepository;
import com.agilis.api.domain.notification.WebhookDispatcher;
import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.provider.*;
import com.agilis.api.domain.user.User;
import com.agilis.api.domain.user.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.UUID;

public class AcceptStoreInviteUseCase {

    private final StoreInviteRepository storeInviteRepository;
    private final StoreMembershipRepository storeMembershipRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final WebhookDispatcher webhookDispatcher;

    public AcceptStoreInviteUseCase(
            StoreInviteRepository storeInviteRepository,
            StoreMembershipRepository storeMembershipRepository,
            ClientRepository clientRepository,
            UserRepository userRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        this.storeInviteRepository      = storeInviteRepository;
        this.storeMembershipRepository  = storeMembershipRepository;
        this.clientRepository           = clientRepository;
        this.userRepository             = userRepository;
        this.webhookDispatcher          = webhookDispatcher;
    }

    public Output execute(Input input) {
        UUID userId = UUID.fromString(input.userId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Regra 1: Somente usuários já cadastrados com CPF podem entrar em uma loja
        if (!clientRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Para entrar em uma loja, é necessário possuir um CPF cadastrado.");
        }

        StoreInvite invite = storeInviteRepository.findByCode(input.code().trim())
                .orElseThrow(() -> new IllegalArgumentException("Convite não encontrado."));

        if (!invite.isValid()) {
            throw new IllegalStateException("Este convite expirou ou já atingiu o limite de usos.");
        }

        // Regra 2: Um usuário não pode entrar na mesma loja 2 vezes
        if (storeMembershipRepository.existsByProviderIdAndStoreId(userId, invite.getStoreId())) {
            throw new IllegalStateException("Você já é membro desta loja.");
        }

        // Regra 3: Um usuário pode estar em N lojas (não há restrição de estar em outras lojas)

        StoreMembership membership = StoreMembership.create(
                invite.getStoreId(),
                userId,
                invite.getRole(),
                invite.getCreatedBy()
        );
        storeMembershipRepository.save(membership);

        invite.consume();
        storeInviteRepository.save(invite);

        webhookDispatcher.dispatch(
                invite.getStoreId(),
                WebhookEventType.STORE_MEMBER_INVITED,
                Map.of(
                        "membershipId", membership.getId().toString(),
                        "providerId", userId.toString(),
                        "role", membership.getRole().name(),
                        "inviteCode", invite.getCode()
                )
        );

        return new Output(
                membership.getId().toString(),
                invite.getStoreId().toString(),
                userId.toString(),
                user.getName(),
                user.getEmail(),
                membership.getRole().name()
        );
    }

    @Schema(name = "AcceptStoreInviteInput")
    public record Input(String userId, String code) {}

    @Schema(name = "AcceptStoreInviteOutput")
    public record Output(
            String membershipId,
            String storeId,
            String userId,
            String name,
            String email,
            String role
    ) {}
}
