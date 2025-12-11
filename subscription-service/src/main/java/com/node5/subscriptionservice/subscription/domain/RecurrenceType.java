package com.node5.subscriptionservice.subscription.domain;

public enum RecurrenceType {
    MONTHLY, WEEKLY;

    public static RecurrenceType from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("RecurrenceType not included"); //빼도될듯
        }

        return java.util.Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid RecurrenceType input: " + value) //빼도될듯
                );
    }
}
