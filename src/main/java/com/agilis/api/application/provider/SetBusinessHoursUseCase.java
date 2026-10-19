package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.BusinessHours;
import com.agilis.api.domain.provider.BusinessHoursRepository;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;
import java.util.UUID;

public class SetBusinessHoursUseCase {

    private final BusinessHoursRepository businessHoursRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public SetBusinessHoursUseCase(BusinessHoursRepository businessHoursRepository, StoreMembershipRepository storeMembershipRepository) {
        this.businessHoursRepository   = businessHoursRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId      = UUID.fromString(input.storeId());

        var membership = storeMembershipRepository.findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        if (!membership.getRole().canManageStore()) {
            throw new IllegalStateException("You do not have permission to configure store hours");
        }

        // upsert: se já tem horário pro dia, atualiza; senão cria
        BusinessHours hours = businessHoursRepository.findByStoreIdAndDayOfWeek(storeId, input.dayOfWeek())
                .map(existing -> {
                    existing.changeHours(input.opensAt(), input.closesAt());
                    return existing;
                })
                .orElse(BusinessHours.create(storeId, input.dayOfWeek(), input.opensAt(), input.closesAt()));

        businessHoursRepository.save(hours);

        return new Output(hours.getId().toString(), hours.getStoreId().toString(), hours.getDayOfWeek(), hours.getOpensAt(), hours.getClosesAt());
    }

    @Schema(name = "SetBusinessHoursInput")
    public record Input(String requesterId, String storeId, int dayOfWeek, LocalTime opensAt, LocalTime closesAt) {}

    @Schema(name = "SetBusinessHourOutput")
    public record Output(String id, String storeId, int dayOfWeek, LocalTime opensAt, LocalTime closesAt) {}
}