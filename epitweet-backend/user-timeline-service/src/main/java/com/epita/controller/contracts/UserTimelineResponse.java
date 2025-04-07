package com.epita.controller.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * A data transfer object representing a user's timeline,
 * intended to be returned in API responses related to the user timeline.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTimelineResponse {
    /**
     * ID of the user.
     */
    private ObjectId userId;

    /**
     * List of timeline entries (created or liked posts).
     */
    private List<UserTimelinePost> userTimeline;
}
