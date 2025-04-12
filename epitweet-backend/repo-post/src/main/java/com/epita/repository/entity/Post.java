package com.epita.repository.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import org.bson.types.ObjectId;

import java.time.Instant;

/**
 * Represents a Post entity in the MongoDB collection "Posts".
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@MongoEntity(collection = "Posts")
public class Post extends PanacheMongoEntityBase {
    /**
     * Unique mongo identifier of the Post
     */
    private ObjectId id;

    /**
     * The ID of the user who created the post.
     */
    private ObjectId userId;

    /**
     * The type of post (post, repost, reply)
     */
    private PostType postType;

    /**
     * The content of the Post (max 160 char)
     */
    private String content;

    /**
     * The URL of the media attached to the post (optional).
     */
    private String mediaUrl;

    /**
     * The ID of the parent post if this is a reply to another post.
     */
    private ObjectId parentId;

    /**
     * Timestamp of creation instant of the post
     */
    private Instant createdAt;

    /**
     * Timestamp of last update instant of the post
     */
    private Instant updatedAt;

    /**
     * Constructs a new Post object.
     *
     * @param userId    The ID of the user who created the post.
     * @param postType  The type of the post.
     * @param content   The content of the post.
     * @param mediaUrl  The URL of the media associated with the post.
     * @param parentId  The ID of the parent post, if any.
     * @param createdAt The timestamp when the post was created.
     * @param updatedAt The timestamp when the post was last updated.
     */
    public Post(ObjectId userId, PostType postType, String content, String mediaUrl,
                ObjectId parentId, Instant createdAt, Instant updatedAt) {

        this.userId = userId;
        this.postType = postType;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.parentId = parentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
