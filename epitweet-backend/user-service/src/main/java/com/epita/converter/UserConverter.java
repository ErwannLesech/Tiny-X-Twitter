package com.epita.converter;

import com.epita.controller.contracts.UserRequest;
import com.epita.controller.contracts.UserResponse;
import com.epita.repository.entity.User;
import org.bson.types.ObjectId;

import java.util.ArrayList;

/**
 * Utility class for converting between UserRequest, UserResponse, and User entity objects.
 */
public class UserConverter {

    /**
     * Converts a UserRequest to a User entity.
     *
     * @param userRequest the UserRequest object to convert
     * @return a User entity containing the converted data
     */
    public static User toEntity(UserRequest userRequest) {
        return new User(
                new ObjectId(),
                userRequest.getTag(),
                userRequest.getPseudo(),
                userRequest.getPassword()
        );
    }

    /**
     * Converts a User entity to a UserResponse.
     *
     * @param user the User entity to convert
     * @return a UserResponse object containing the converted data
     */
    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user._id,
                user.tag,
                user.pseudo,
                user.password
        );
    }
}
