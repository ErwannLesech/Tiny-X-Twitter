package com.epita.repository.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import org.bson.types.ObjectId;

import java.time.Instant;

/**
 * Represents a User entity stored in the MongoDB collection "Users".
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@MongoEntity(collection = "Users")
public class User extends PanacheMongoEntityBase {

    /**
     * The unique identifier of the user.
     */
    private ObjectId id;

    /**
     * A tag associated with the user.
     */
    private String tag;

    /**
     * The pseudonym or username of the user.
     */
    private String pseudo;

    /**
     * The hashed password of the user.
     */
    private String password;

    /**
     * The url of profilePicture image
     */
    private String profilePictureUrl;

    /**
     * The url of profile banner image
     */
    private String profileBannerUrl;

    /**
     * The content of profile description (limited to 255 char)
     */
    private String profileDescription;

    /**
     * Date of creation of the account
     */
    private Instant createdAt;

}
