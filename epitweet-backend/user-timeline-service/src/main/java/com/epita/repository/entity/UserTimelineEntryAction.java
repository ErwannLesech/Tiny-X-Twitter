package com.epita.repository.entity;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the actions performed by a user on their timeline post.
 */
public enum UserTimelineEntryAction {
    /**
     * Indicates that the user has created the post.
     */
    CREATE("created"),

    /**
     * Indicates that the user has liked the post.
     */
    LIKE("liked");

    /**
     * The string representation of the action ("created" or "liked").
     */
    private final String value;

    UserTimelineEntryAction(String value) { this.value = value; }

    /**
     * Returns the {@code UserTimelinePostAction} enum constant corresponding to the specified string value.
     *
     * @param value The string value to convert to a {@code UserTimelinePostAction}.
     * @return The {@code UserTimelinePostAction} enum constant corresponding to the specified string value.
     * @throws IllegalArgumentException If the specified string value does not match any {@code UserTimelinePostAction}
     * enum constant.
     */
    public static UserTimelineEntryAction fromString(String value) {
        for (UserTimelineEntryAction userTimelineEntryAction : UserTimelineEntryAction.values()) {
            if (userTimelineEntryAction.getValue().equalsIgnoreCase(value)) {
                return userTimelineEntryAction;
            }
        }
        throw new IllegalArgumentException("No enum constant for value " + value);
    }

    /**
     * Returns the string value of the {@code UserTimelinePostAction}.
     *
     * @return The string value of the {@code UserTimelinePostAction}.
     */
    private String getValue() { return this.value; }

    /**
     * Returns the string value of the {@code UserTimelinePostAction}.
     *
     * @return The string value of the {@code UserTimelinePostAction}.
     */
    @JsonValue
    @Override
    public String toString() { return value; }
}
