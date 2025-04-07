package com.epita.payloads.homeTimeline;

/**
 * - ObjectId : userId
 * - ObjectId : userFollowedId
 * - String : method ('follow', 'unfollow')
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import java.util.Date;

/**
 * Represents a payload for home timeline srvc from social srvc to communicate a follow request.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SocialHomeTimelineFollow {
    /**
     *  The id of the source user
     */
    private ObjectId userId;

    /**
     *  The id of the targetted user
     */
    private ObjectId userFollowedId;

    /**
     *  The method (follow, unfollow)
     */
    private String method;
}