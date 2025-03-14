package com.epita.repository.entity;

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
}

