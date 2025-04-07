package com.epita.contracts.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.time.Instant;

/**
 * Represents a response containing user details.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserResponse {

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
     * The password of the user.
     */
    public String password;

    /**
     * The url of profilePicture image
     */
    public String profilePictureUrl;

    /**
     * The url of profile banner image
     */
    public String profileBannerUrl;

    /**
     * The content of profile description (limited 255 char)
     */
    public String profileDescription;

    /**
     * Date of creation of the account
     */
    public Instant createdAt;
}
