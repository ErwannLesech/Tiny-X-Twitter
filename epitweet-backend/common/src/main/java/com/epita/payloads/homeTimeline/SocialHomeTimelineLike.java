package com.epita.payloads.homeTimeline;

/**
 * - ObjectId : userId
 * - ObjectId : postType
 * - Date : postLikeDate
 * - String : method ('like', 'unlike')
 */

import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

/**
 * Represents a payload for home timeline srvc from social srvc to indicate when a like request is made.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SocialHomeTimelineLike {
    /**
     *  The id of the user
     */
    private ObjectId userId;

    /**
     *  The id of the post
     */
    private ObjectId postId;

    /**
     *  The date when the post was liked or unliked
     */
    private LocalDateTime postLikeDate;

    /**
     *  The method (like, unlike)
     */
    private String method;
}