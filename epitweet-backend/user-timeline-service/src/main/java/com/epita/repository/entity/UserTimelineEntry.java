package com.epita.repository.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import org.bson.types.ObjectId;

import java.time.Instant;

/**
 * Represents a post in a user's timeline.
 * This entity is stored in the {@code user-timeline} MongoDB collection.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@MongoEntity(collection = "user-timeline", database = "Epitweet")
public class UserTimelineEntry implements Comparable<UserTimelineEntry> {
    /**
     * The ID of the user.
     */
    private ObjectId userId;

    /**
     * The ID of the post.
     */
    private String postId;

    /**
     * The type of action performed by the user (create or like).
     */
    private UserTimelineEntryAction userTimelineEntryAction;

    /**
     * The exact timestamp when the user performed the action.
     */
    private Instant performedAt;

    /**
     * Compares this {@code UserTimelinePost} to another based on the {@code performedAt} timestamp.
     *
     * @param userTimelineEntry The other {@code UserTimelinePost} to compare to.
     * @return A negative integer, zero, or a positive integer as this object's {@code performedAt}
     *         is before, equal to, or after the specified object's {@code performedAt}.
     */
    @Override
    public int compareTo(UserTimelineEntry userTimelineEntry) {
        return this.performedAt.compareTo(userTimelineEntry.performedAt);
    }
}
