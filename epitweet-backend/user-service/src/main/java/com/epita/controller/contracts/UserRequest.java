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
     * A list of user IDs that this user has blocked.
     */
    List<ObjectId> blockedUsers;
}
