package com.epita.payloads.userTimeline;

/**
 * - ObjectId : userId
 * - String : postId
 * - DateTime : postModificationDate
 * - String : method ('creation', 'deletion')
 */

import lombok.*;
import org.bson.types.ObjectId;

import java.time.Instant;

/**
 * Represents a payload for User Timeline from repo-post to add created or deleted post to timeline
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostTimeline {
    /**
     *  The id of the user who created or deleted the post.
     */
    private ObjectId userId;

    /**
     * The id of the created or deleted post.
     */
    private String postId;

    /**
     * The modification instant of the post
     */
    private Instant postModificationDate;

    /**
     * The method (creation or deletion)
     */
    private String method;
}