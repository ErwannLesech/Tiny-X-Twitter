package com.epita.repository.entity;

import com.epita.contracts.post.PostResponse;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import org.bson.types.ObjectId;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@MongoEntity(collection = "Users")
public class HomeTimelineEntry {
    enum type {
        POST,
        LIKE,
    }

    ObjectId userId;

    ObjectId userFollowedId;

    type type;

    PostResponse postResponse;

}