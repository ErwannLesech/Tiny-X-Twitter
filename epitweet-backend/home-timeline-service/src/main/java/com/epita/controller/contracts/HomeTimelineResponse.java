package com.epita.controller.contracts;

import com.epita.contracts.post.PostResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Response to get home user timeline
 */
@NoArgsConstructor
@AllArgsConstructor
public class HomeTimelineResponse {
    /**
     * The ID of the user's timeline.
     */
    ObjectId userId;

    /**
     * User's timeline.
     */
    List<HomeTimelinePost> timeline;
}
