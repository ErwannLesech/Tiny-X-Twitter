package com.epita.repository.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SentimentType {
    /**
     * Represents a positive post
     */
    POSITIVE("positive"),

    /**
     * Represents a neutral post
     */
    NEUTRAL("neutral"),

    /**
     * Represents a negative post.
     */
    NEGATIVE("negative");

    private final String value;

    SentimentType(String value) {
        this.value = value;
    }

    /**
     * Returns the {@code SentimentType} enum constant corresponding to the specified string value.
     *
     * @param text The string value to convert to a {@code SentimentType}.
     * @return The {@code SentimentType} enum constant corresponding to the specified string value.
     * @throws IllegalArgumentException If the specified string value does not match any {@code SentimentType}
     * enum constant.
     */
    public static SentimentType fromString(String text) {
        for (SentimentType sentimentType : SentimentType.values()) {
            if (sentimentType.getValue().equalsIgnoreCase(text)) {
                return sentimentType;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + text);
    }

    /**
     * Returns the string value of the {@code SentimentType}.
     *
     * @return The string value of the {@code SentimentType}.
     */
    private String getValue() {
        return this.value;
    }

    /**
     * Returns the string value of the {@code SentimentType}.
     *
     * @return The string value of the {@code SentimentType}.
     */
    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
