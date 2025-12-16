package com.node5.subscriptionservice.subscription.domain;

public enum RecurrenceType {
    MONTHLY, WEEKLY;

    public static RecurrenceType from(String value) {
        return java.util.Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
