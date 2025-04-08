package com.epita.repository.entity;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum to specify a HomeTimelineEntry type.
 */
public enum EntryType {
    POST("post"),
    LIKE("like"),;

    private final String value;

    EntryType(final String value) {
        this.value = value;
    }

    /**
     * Returns the string value of the {@code EntryType}.
     *
     * @return The string value of the {@code EntryType}.
     */
    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
