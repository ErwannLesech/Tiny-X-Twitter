package com.epita.controller.contracts;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Represents a user request containing user details.
 */
@Getter
@Setter
@ToString
public class UserRequest {

    /**
     * A tag associated with the user.
     */
    String tag;

    /**
     * The pseudonym or username of the user.
     */
    String pseudo;

    /**
     * The password of the user.
     */
    String password;

    /**
     * The url of profilePicture image
     */
    String profilePictureUrl;

    /**
     * The url of profile banner image
     */
    String profileBannerUrl;

    /**
     * The content of profile description (limited 255 char)
     */
    String profileDescription;
}
