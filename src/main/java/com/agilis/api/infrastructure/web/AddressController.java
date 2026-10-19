package com.agilis.api.infrastructure.web;

import com.agilis.api.application.user.AddAddressUseCase;
import com.agilis.api.application.user.UpdateAddressUseCase;
import com.agilis.api.domain.user.Address;
import com.agilis.api.domain.user.AddressRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me/addresses")
public class AddressController {

    private final AddAddressUseCase addAddressUseCase;
    private final UpdateAddressUseCase updateAddressUseCase;
    private final AddressRepository addressRepository;

    public AddressController(
            AddAddressUseCase addAddressUseCase,
            UpdateAddressUseCase updateAddressUseCase,
            AddressRepository addressRepository
    ) {
        this.addAddressUseCase    = addAddressUseCase;
        this.updateAddressUseCase = updateAddressUseCase;
        this.addressRepository    = addressRepository;
    }

    @GetMapping
    public ResponseEntity<List<Address>> getMyAddresses() {
        String userId = getCurrentUserId();
        return ResponseEntity.ok(addressRepository.findAllByUserId(UUID.fromString(userId)));
    }

    @PostMapping
    public ResponseEntity<AddAddressUseCase.Output> add(
            @RequestBody AddAddressUseCase.Input input
    ) {
        String userId = getCurrentUserId();
        AddAddressUseCase.Input inputWithUser = new AddAddressUseCase.Input(
                userId,
                input.street(),
                input.number(),
                input.complement(),
                input.city(),
                input.state(),
                input.cep()
        );
        return ResponseEntity.ok(addAddressUseCase.execute(inputWithUser));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<UpdateAddressUseCase.Output> update(
            @PathVariable String addressId,
            @RequestBody UpdateAddressUseCase.Input input
    ) {
        String userId = getCurrentUserId();
        UpdateAddressUseCase.Input inputWithUser = new UpdateAddressUseCase.Input(
                userId,
                addressId,
                input.street(),
                input.number(),
                input.complement(),
                input.city(),
                input.state(),
                input.cep()
        );
        return ResponseEntity.ok(updateAddressUseCase.execute(inputWithUser));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> delete(@PathVariable String addressId) {
        String userId = getCurrentUserId();
        Address address = addressRepository.findById(UUID.fromString(addressId))
                .orElseThrow(() -> new IllegalArgumentException("Address not found."));

        if (!address.getUserId().equals(UUID.fromString(userId))) {
            throw new IllegalStateException("No permission to delete this address.");
        }

        addressRepository.deleteById(UUID.fromString(addressId));
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}