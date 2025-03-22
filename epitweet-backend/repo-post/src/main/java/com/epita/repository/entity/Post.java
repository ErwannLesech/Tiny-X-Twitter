package com.epita.repository.entity;

import com.epita.controller.contracts.PostRequest;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.time.Instant;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@MongoEntity(collection="Posts")
public class Post {
    public ObjectId _id;
    public ObjectId userId;
    public PostType postType;
    public String content;
    public String mediaUrl;
    public ObjectId parentId;
    public Instant createdAt;
    public Instant updatedAt;

    public Post(ObjectId userId, PostRequest postRequest) {
        this.userId = userId;
        this.postType = PostType.fromString(postRequest.getPostType());
        this.content = postRequest.content;
        this.mediaUrl = postRequest.mediaUrl;
        this.parentId = postRequest.getParentObjectId();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}

