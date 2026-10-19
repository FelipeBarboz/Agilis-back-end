package com.agilis.api.infrastructure.persistence.service;

import com.agilis.api.domain.service.ServiceCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Conversor JPA que normaliza o valor do banco para uppercase antes de fazer
 * o Enum.valueOf(), garantindo compatibilidade com dados legados gravados em
 * lowercase (ex: "reparos" → ServiceCategory.REPAROS).
 */
@Converter(autoApply = false)
public class ServiceCategoryConverter implements AttributeConverter<ServiceCategory, String> {

    @Override
    public String convertToDatabaseColumn(ServiceCategory attribute) {
        if (attribute == null) return null;
        return attribute.name(); // sempre grava em UPPERCASE
    }

    @Override
    public ServiceCategory convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        return ServiceCategory.valueOf(dbData.trim().toUpperCase());
    }
}
