package com.epita.repository.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PostType {
    POST("post"),
    REPLY("reply"),
    REPOST("repost");

    private final String value;

    PostType(String value) {
        this.value = value;
    }

    public static PostType fromString(String text) {
        for (PostType postType : PostType.values()) {
            if (postType.getValue().equalsIgnoreCase(text)) {
                return postType;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + text);
    }

    private String getValue() {
        return this.value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}