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

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}