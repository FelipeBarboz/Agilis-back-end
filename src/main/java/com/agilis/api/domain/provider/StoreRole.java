package com.agilis.api.domain.provider;

public enum StoreRole {
    OWNER,
    ADMIN,
    EMPLOYEE;

    public boolean canManageStore() {
        return this == OWNER || this == ADMIN;
    }

    public boolean canManageMembers() {
        return this == OWNER;
    }

    public boolean canManageServices() {
        return this == OWNER || this == ADMIN;
    }
}