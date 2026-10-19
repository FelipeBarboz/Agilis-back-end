package com.agilis.api.infrastructure.persistence.provider;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "store_positions")
public class StorePositionEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "can_manage_bookings", nullable = false)
    private boolean canManageBookings;

    @Column(name = "can_access_chats", nullable = false)
    private boolean canAccessChats;

    @Column(name = "can_manage_store_settings", nullable = false)
    private boolean canManageStoreSettings;

    @Column(name = "can_view_reports", nullable = false)
    private boolean canViewReports;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}