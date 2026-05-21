package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.*;
import com.agilis.api.domain.user.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class InviteMemberUseCase {

    private final StoreMembershipRepository storeMembershipRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;

    public InviteMemberUseCase(
            StoreMembershipRepository storeMembershipRepository,
            ProviderRepository providerRepository,
            UserRepository userRepository
    ) {
        this.storeMembershipRepository = storeMembershipRepository;
        this.providerRepository        = providerRepository;
        this.userRepository            = userRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());

        // verifica se quem está convidando tem permissão
        StoreMembership requesterMembership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("Você não é membro desta loja"));

        if (!requesterMembership.getRole().canManageMembers() &&
                !requesterMembership.getRole().canManageStore()) {
            throw new IllegalStateException("Sem permissão para convidar membros");
        }

        // busca o provider pelo email
        var user = userRepository.findByEmail(input.providerEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        providerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Este usuário não possui conta de provider"));

        // verifica se já é membro
        if (storeMembershipRepository.existsByProviderIdAndStoreId(user.getId(), storeId)) {
            throw new IllegalStateException("Este provider já é membro da loja");
        }

        // employee não pode convidar admin
        if (input.role() == StoreRole.OWNER) {
            throw new IllegalStateException("Não é possível convidar alguém como owner");
        }

        if (input.role() == StoreRole.ADMIN &&
                requesterMembership.getRole() != StoreRole.OWNER) {
            throw new IllegalStateException("Apenas o owner pode convidar admins");
        }

        StoreMembership membership = StoreMembership.create(
                storeId,
                user.getId(),
                input.role(),
                requesterId
        );
        storeMembershipRepository.save(membership);

        return new Output(
                membership.getId().toString(),
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                membership.getRole().name()
        );
    }

    @Schema(name = "InviteMemberInput")
    public record Input(
            String requesterId,
            String storeId,
            String providerEmail,
            StoreRole role
    ) {}

    @Schema(name = "InviteMemberOutput")
    public record Output(
            String membershipId,
            String providerId,
            String name,
            String email,
            String role
    ) {}
}