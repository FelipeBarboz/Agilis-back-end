package com.agilis.api.domain.provider;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class StorePosition {

    private final UUID id;
    private final UUID storeId;
    private String title;
    private String description;
    private boolean canManageBookings;
    private boolean canAccessChats;
    private boolean canManageStoreSettings;
    private boolean canViewReports;
    private final LocalDateTime createdAt;

    private StorePosition(UUID id, UUID storeId, String title, String description,
                          boolean canManageBookings, boolean canAccessChats,
                          boolean canManageStoreSettings, boolean canViewReports, LocalDateTime createdAt) {
        this.id                     = id;
        this.storeId                = storeId;
        this.title                  = validateTitle(title);
        this.description            = description;
        this.canManageBookings      = canManageBookings;
        this.canAccessChats         = canAccessChats;
        this.canManageStoreSettings = canManageStoreSettings;
        this.canViewReports         = canViewReports;
        this.createdAt              = createdAt;
    }

    public static StorePosition create(UUID storeId, String title, String description,
                                       boolean canManageBookings, boolean canAccessChats,
                                       boolean canManageStoreSettings, boolean canViewReports) {
        return new StorePosition(UUID.randomUUID(), storeId, title, description,
                canManageBookings, canAccessChats, canManageStoreSettings, canViewReports, LocalDateTime.now());
    }

    public static StorePosition reconstitute(UUID id, UUID storeId, String title, String description,
                                             boolean canManageBookings, boolean canAccessChats,
                                             boolean canManageStoreSettings, boolean canViewReports, LocalDateTime createdAt) {
        return new StorePosition(id, storeId, title, description,
                canManageBookings, canAccessChats, canManageStoreSettings, canViewReports, createdAt);
    }

    public void update(String title, String description, boolean canManageBookings, boolean canAccessChats,
                       boolean canManageStoreSettings, boolean canViewReports) {
        this.title                  = validateTitle(title);
        this.description            = description;
        this.canManageBookings      = canManageBookings;
        this.canAccessChats         = canAccessChats;
        this.canManageStoreSettings = canManageStoreSettings;
        this.canViewReports         = canViewReports;
    }

    private String validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        return title;
    }
}