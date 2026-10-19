package com.agilis.api.infrastructure.web;

import com.agilis.api.application.coupon.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CreateCouponUseCase createCouponUseCase;
    private final ListCouponsUseCase listCouponsUseCase;
    private final DeactivateCouponUseCase deactivateCouponUseCase;
    private final ValidateCouponUseCase validateCouponUseCase;

    public CouponController(
            CreateCouponUseCase createCouponUseCase,
            ListCouponsUseCase listCouponsUseCase,
            DeactivateCouponUseCase deactivateCouponUseCase,
            ValidateCouponUseCase validateCouponUseCase
    ) {
        this.createCouponUseCase     = createCouponUseCase;
        this.listCouponsUseCase      = listCouponsUseCase;
        this.deactivateCouponUseCase = deactivateCouponUseCase;
        this.validateCouponUseCase   = validateCouponUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateCouponUseCase.Output> create(@RequestBody CreateCouponUseCase.Input input) {
        String requesterId = getCurrentUserId();
        var inputWithRequester = new CreateCouponUseCase.Input(
                requesterId, input.code(), input.description(), input.discountType(), input.discountValue(),
                input.maxDiscountAmount(), input.minOrderValue(), input.validFrom(), input.validUntil()
        );
        return ResponseEntity.ok(createCouponUseCase.execute(inputWithRequester));
    }

    @GetMapping
    public ResponseEntity<List<CreateCouponUseCase.Output>> list() {
        return ResponseEntity.ok(listCouponsUseCase.execute(getCurrentUserId()));
    }

    @PatchMapping("/{couponId}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable String couponId) {
        deactivateCouponUseCase.execute(getCurrentUserId(), couponId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidateCouponUseCase.Output> validate(@RequestBody ValidateCouponUseCase.Input input) {
        String clientId = getCurrentUserId();
        var inputWithClient = new ValidateCouponUseCase.Input(clientId, input.serviceId(), input.code());
        return ResponseEntity.ok(validateCouponUseCase.execute(inputWithClient));
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}