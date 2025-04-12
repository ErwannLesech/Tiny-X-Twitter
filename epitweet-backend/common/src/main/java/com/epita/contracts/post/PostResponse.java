package com.epita.contracts.post;

import lombok.*;
import org.bson.types.ObjectId;

import java.time.Instant;

/**
 * Represents the response for a Post entity, used in controller contracts and in repo-post payloads
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostResponse {
    public ObjectId _id;
    public ObjectId userId;
    public String postType;
    public String content;
    public String mediaUrl;
    public ObjectId parentId;
    public Instant createdAt;
    public Instant updatedAt;
}
