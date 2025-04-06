package com.epita.repository.entity;

import com.epita.contracts.post.PostResponse;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;

import org.bson.types.ObjectId;

import java.time.Instant;

/**
 * Represents a HomeTimelineEntry entity in the MongoDB collection "HomeTimelineEntries".
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@MongoEntity(collection = "HomeTimelineEntries")
public class HomeTimelineEntry {

    /**
     * Unique mongo identifier of the HomeTimelineEntries
     */
    ObjectId _id;

    /**
     * The ID of the user's timeline.
     */
    ObjectId userId;

    /**
     * The ID of the user followed by timeline owner.
     */
    ObjectId userFollowedId;

    /**
     * The type of the post liked or posted by the followed user.
     */
    EntryType type;

    /**
     * One post from the user's timeline.
     */
    PostResponse post;

    /**
     * Date when the post have been posted or liked by the followed user.
     */
    Instant date;

}