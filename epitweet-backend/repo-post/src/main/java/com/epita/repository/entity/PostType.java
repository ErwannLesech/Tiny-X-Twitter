package com.epita.repository.entity;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the type of post.
 */
public enum PostType {
    /**
     * Represents a regular post.
     */
    POST("post"),

    /**
     * Represents a reply to a post.
     */
    REPLY("reply"),

    /**
     * Represents a repost of an existing post.
     */
    REPOST("repost");

    private final String value;

    PostType(String value) {
        this.value = value;
    }

    /**
     * Returns the {@code PostType} enum constant corresponding to the specified string value.
     *
     * @param text The string value to convert to a {@code PostType}.
     * @return The {@code PostType} enum constant corresponding to the specified string value.
     * @throws IllegalArgumentException If the specified string value does not match any {@code PostType} enum constant.
     */
    public static PostType fromString(String text) {
        for (PostType postType : PostType.values()) {
            if (postType.getValue().equalsIgnoreCase(text)) {
                return postType;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + text);
    }

    /**
     * Returns the string value of the {@code PostType}.
     *
     * @return The string value of the {@code PostType}.
     */
    private String getValue() {
        return this.value;
    }

    /**
     * Returns the string value of the {@code PostType}.
     *
     * @return The string value of the {@code PostType}.
     */
    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
