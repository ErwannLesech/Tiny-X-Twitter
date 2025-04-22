package com.epita.repository.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import org.bson.types.ObjectId;

/**
 * Represents a Post entity in the MongoDB collection "Posts".
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@MongoEntity(collection = "Sentiments")
public class Sentiment extends PanacheMongoEntityBase {
    /**
     * Unique mongo id of the sentiment
     */
    private ObjectId id;

    /**
     * Unique identifier of the Post
     */
    private ObjectId postId;

    /**
     * The content of the Post (max 160 char)
     */
    private String content;

    /**
     * The sentiment analysed of the content
     */
    private String sentiment;
}
