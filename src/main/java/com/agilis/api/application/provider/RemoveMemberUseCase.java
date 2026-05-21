package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.StoreMembership;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.provider.StoreRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class RemoveMemberUseCase {

    private final StoreMembershipRepository storeMembershipRepository;

    public RemoveMemberUseCase(StoreMembershipRepository storeMembershipRepository) {
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public void execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());
        UUID memberId    = UUID.fromString(input.memberId());

        // verifica permissão de quem está removendo
        StoreMembership requesterMembership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("Você não é membro desta loja"));

        if (!requesterMembership.getRole().canManageStore()) {
            throw new IllegalStateException("Sem permissão para remover membros");
        }
        StoreMembership targetMembership = storeMembershipRepository
                .findByProviderIdAndStoreId(memberId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("Membro não encontrado na loja"));

        // owner não pode ser removido
        if (targetMembership.getRole() == StoreRole.OWNER) {
            throw new IllegalStateException("Owner não pode ser removido da loja");
        }

        // admin só pode ser removido pelo owner
        if (targetMembership.getRole() == StoreRole.ADMIN &&
                requesterMembership.getRole() != StoreRole.OWNER) {
            throw new IllegalStateException("Apenas o owner pode remover admins");
        }

        storeMembershipRepository.deleteById(targetMembership.getId());
    }

    @Schema(name = "RemoveMemberInput")
    public record Input(String requesterId, String storeId, String memberId) {}
}