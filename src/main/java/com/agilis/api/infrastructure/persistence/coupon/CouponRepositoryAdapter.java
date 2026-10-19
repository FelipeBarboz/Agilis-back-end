package com.agilis.api.infrastructure.persistence.coupon;

import com.agilis.api.domain.coupon.Coupon;
import com.agilis.api.domain.coupon.CouponRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CouponRepositoryAdapter implements CouponRepository {

    private final CouponJpaRepository jpaRepository;

    public CouponRepositoryAdapter(CouponJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Coupon save(Coupon coupon) {
        jpaRepository.save(toEntity(coupon));
        return coupon;
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        return jpaRepository.findByCodeIgnoreCase(code).map(this::toDomain);
    }

    @Override
    public List<Coupon> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private CouponEntity toEntity(Coupon c) {
        CouponEntity entity = new CouponEntity();
        entity.setId(c.getId());
        entity.setCode(c.getCode());
        entity.setDescription(c.getDescription());
        entity.setDiscountType(c.getDiscountType());
        entity.setDiscountValue(c.getDiscountValue());
        entity.setMaxDiscountAmount(c.getMaxDiscountAmount());
        entity.setMinOrderValue(c.getMinOrderValue());
        entity.setValidFrom(c.getValidFrom());
        entity.setValidUntil(c.getValidUntil());
        entity.setActive(c.isActive());
        entity.setCreatedAt(c.getCreatedAt());
        return entity;
    }

    private Coupon toDomain(CouponEntity entity) {
        return Coupon.reconstitute(
                entity.getId(), entity.getCode(), entity.getDescription(), entity.getDiscountType(), entity.getDiscountValue(),
                entity.getMaxDiscountAmount(), entity.getMinOrderValue(), entity.getValidFrom(), entity.getValidUntil(),
                entity.isActive(), entity.getCreatedAt()
        );
    }
}