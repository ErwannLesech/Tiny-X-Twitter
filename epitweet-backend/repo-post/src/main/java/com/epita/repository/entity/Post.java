package com.epita.repository.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.time.Instant;

/**
 * Represents a Post entity in the MongoDB collection "Posts".
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@MongoEntity(collection = "Posts")
public class Post {

    public ObjectId _id;
    public ObjectId userId;
    public PostType postType;
    public String content;
    public String mediaUrl;
    public ObjectId parentId;
    public Instant createdAt;
    public Instant updatedAt;

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
