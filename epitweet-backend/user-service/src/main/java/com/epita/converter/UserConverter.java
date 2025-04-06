package com.epita.converter;

import com.epita.controller.contracts.UserRequest;
import com.epita.contracts.user.UserResponse;
import com.epita.payloads.user.DeleteUserPost;
import com.epita.repository.entity.User;
import org.bson.types.ObjectId;

import java.time.Instant;

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
                userRequest.getPassword(),
                userRequest.getProfilePictureUrl(),
                userRequest.getProfileBannerUrl(),
                userRequest.getProfileDescription(),
                Instant.now()
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
                user.getId(),
                user.getTag(),
                user.getPseudo(),
                user.getPassword(),
                user.getProfilePictureUrl(),
                user.getProfileBannerUrl(),
                user.getProfileDescription(),
                user.getCreatedAt()
        );
    }

    public static DeleteUserPost toDeleteResponse(User user) {
        return new DeleteUserPost(
                user.getId()
        );
    }
}
