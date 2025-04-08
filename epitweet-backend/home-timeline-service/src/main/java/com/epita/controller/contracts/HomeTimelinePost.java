package com.epita.controller.contracts;

import com.epita.repository.entity.EntryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    Instant postOrLikeTime;
}
