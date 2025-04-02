package com.epita.controller.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.util.List;

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
    ObjectId _id;

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
}
