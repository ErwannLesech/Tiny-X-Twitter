package com.epita.payloads.post;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

/**
 * Represents a response to create a new post.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreatePostResponse {

    /**
     * The ID of the user who created the post.
     */
    private ObjectId userId;

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
    private String mediaUrl;

    /**
     * The ID of the parent post if this is a reply to another post.
     */
    private ObjectId parentId;

    /**
     * A flag indicating whether the parent user blocked the current user.
     */
    private Boolean parentUserBlockedUser;

    /**
     * A flag indicating whether the current user blocked the parent user.
     */
    private Boolean userBlockedParentUser;
}
