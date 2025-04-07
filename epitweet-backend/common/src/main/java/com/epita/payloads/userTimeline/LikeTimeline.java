package com.epita.payloads.userTimeline;

/**
 * - ObjectId : userId
 * - String : postType
 * - Date : postLikeDate
 * - String : method ('like', 'unlike')
 */

import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

/**
 * Represents a payload for user timeline srvc from social srvc to indicate when a like request is made.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LikeTimeline {
    /**
     *  The id of the user
     */
    private ObjectId userId;

    /**
     *  The id of the post
     */
    private String postId;

    /**
     *  The date when the post was liked or unliked
     */
    private LocalDateTime postLikeDate;

    /**
     *  The method (like, unlike)
     */
    private String method;
}