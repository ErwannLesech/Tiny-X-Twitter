package com.epita.repository.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

/**
 * Represents a User entity stored in the MongoDB collection "Users".
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@MongoEntity(collection = "Users")
public class User {

    /**
     * The unique identifier of the user.
     */
    public ObjectId _id;

    /**
     * A tag associated with the user.
     */
    public String tag;

    /**
     * The pseudonym or username of the user.
     */
    public String pseudo;

    /**
     * The hashed password of the user.
     */
    public String password;
}
