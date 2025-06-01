package com.tanzeem.purchase.enums;

public enum PurchaseStatus {
    PENDING,
    COMPLETED,
    CANCELLED,
    RECEIVED,
    PARTIALLY_RECEIVED;

    public static PurchaseStatus fromString(String status) {
        if (status == null) {
            return null;
        }
        try {
            return PurchaseStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // or throw an exception if preferred
        }
    }
}
