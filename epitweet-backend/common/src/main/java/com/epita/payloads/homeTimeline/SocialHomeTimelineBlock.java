package com.epita.payloads.homeTimeline;

/**
 * - ObjectId : userId
 * - ObjectId : userBlockedId
 * - String : method ('block', 'unblock')
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import java.util.Date;

/**
 * Represents a payload for home timeline srvc from social srvc to communicate a block request.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SocialHomeTimelineBlock {
    /**
     *  The id of the source user
     */
    private ObjectId userId;

    /**
     *  The id of the targetted user
     */
    private ObjectId userBlockedId;

    /**
     *  The method (block, unblock)
     */
    private String method;
}