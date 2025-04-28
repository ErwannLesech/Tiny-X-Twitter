package com.epita.controller.contracts;

import com.epita.repository.entity.EntryType;
import lombok.*;
import org.bson.types.ObjectId;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class HomeTimelinePost {
    /**
     * The ID of the user's post.
     */
    ObjectId userId;

    /**
     * The ID of the post.
     */
    ObjectId postId;

    /**
     * The type of the post liked or posted by the user.
     */
    EntryType type;

    /**
     * Is suggestion or already on basic timeline
     */
    Boolean isSuggestion;

    Instant postOrLikeTime;
}
