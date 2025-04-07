package com.epita.payloads.search;

/**
 * - String : postId
 * - String : postType - String : content - String : mediaPath
 * - ObjectId : parentId
 * - String : method ('creation', 'deletion')
 */

import lombok.*;
import org.bson.types.ObjectId;

/**
 * Represents a payload for search srvc from repo-post to index created or deleted post.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IndexPost {
    /**
     *  The id of the post
     */
    private String postId;

    /**
     * The type of the post (post, repost, reply).
     */
    private String postType;

    /**
     * The content of the post.
     */
    private String content;

    /**
     * The URL of the media attached to the post (if any).
     */
    private String mediaPath;

    /**
     * The ID of the parent post if this is a reply to another post.
     */
    private ObjectId parentId;

    /**
     * The method (creation, deletion)
     */
    private String method;
}
