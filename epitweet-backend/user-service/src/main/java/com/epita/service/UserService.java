package com.epita.service;

import com.epita.controller.contracts.UserRequest;
import com.epita.contracts.user.UserResponse;
import com.epita.converter.UserConverter;
import com.epita.repository.UserRepository;
import com.epita.repository.entity.User;
import com.epita.repository.publisher.DeleteUserPublisher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    Logger logger;

    @Inject
    DeleteUserPublisher deleteUserPublisher;

    /**
     * Retrieves a user by their tag.
     *
     * @param userTag the tag of the user
     * @return a UserResponse object if the user is found, or null
     */
    public UserResponse getUser(String userTag) {
        User user = userRepository.findByTag(userTag);
        logger.infof("getting User: %s", user);
        if (user == null) {
            return null;
        }

        return UserConverter.toResponse(user);
    }

    /**
     * Retrieves a user by their Identifier.
     *
     * @param userId the id of the user
     * @return a UserResponse object if the user is found, or null
     */
    public UserResponse getUserById(ObjectId userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return null;
        }

        return UserConverter.toResponse(user);
    }

    /**
     * Creates a new user based on the provided request.
     *
     * @param userRequest the user request containing user details
     * @return a UserResponse object for the created user, or null if the tag already exists
     */
    public UserResponse createUser(final UserRequest userRequest) {
        if (userRepository.findByTag(userRequest.getTag()) == null) {

            // Password hash handling
            if (userRequest.getPassword() != null) {
                userRequest.setPassword(hashPassword(userRequest.getPassword()));
            }

            User newUser = UserConverter.toEntity(userRequest);
            userRepository.createUser(newUser);

            return UserConverter.toResponse(newUser);
        }

        return null;
    }

    /**
     * Updates an existing user based on the provided request.
     *
     * @param userRequest the user request containing updated user details
     * @return true if the user was updated successfully, false otherwise
     */
    public UserResponse updateUser(final UserRequest userRequest) {
        User userToUpdate = userRepository.findByTag(userRequest.getTag());
        if (userToUpdate != null) {
            if (userRequest.getPseudo() != null && !userRequest.getPseudo().isEmpty()){
                userToUpdate.setPseudo(userRequest.getPseudo());
            }
            if (userRequest.getProfileDescription() != null && !userRequest.getProfileDescription().isEmpty()) {
                userToUpdate.setProfileDescription(userRequest.getProfileDescription());
            }
            if (userRequest.getProfilePictureUrl() != null && !userRequest.getProfilePictureUrl().isEmpty()) {
                userToUpdate.setProfilePictureUrl(userRequest.getProfilePictureUrl());
            }
            if (userRequest.getProfileBannerUrl() != null && !userRequest.getProfileBannerUrl().isEmpty()) {
                userToUpdate.setProfileBannerUrl(userRequest.getProfileBannerUrl());
            }

            // Password hash handling
            if (userRequest.getPassword() != null) {
                userToUpdate.setPassword(hashPassword(userRequest.getPassword()));
            }

            userRepository.updateUser(userToUpdate);
            return UserConverter.toResponse(userToUpdate);
        }

        return null;
    }

    /**
     * Deletes a user by their tag.
     *
     * @param userTag the tag of the user to delete
     * @return a UserResponse object for the deleted user, or null if the user was not found
     */
    public UserResponse deleteUser(final String userTag) {
        User userToDelete = userRepository.findByTag(userTag);
        if (userToDelete != null) {
            userRepository.deleteUser(userToDelete);

            // remove its posts
            deleteUserPublisher.publish(UserConverter.toDeleteResponse(userToDelete));

            return UserConverter.toResponse(userToDelete);
        }

        return null;
    }

    /**
     * Authenticates a user based on the provided request.
     *
     * @param userRequest the user request containing authentication details
     * @return 404 if the user is unknown, 401 if the password doesn't match, 200 if it matches
     */
    public Integer authUser(UserRequest userRequest) {
        User user = userRepository.findByTag(userRequest.getTag());

        if (user == null) {
            return 404;
        }

        if (checkPassword(userRequest.getPassword(), user.getPassword())) {
            return 200;
        }

        return 401;
    }

    /**
     * Hashes a password using BCrypt.
     *
     * @param password the password to hash
     * @return the hashed password
     */
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Checks if a password matches the hashed password.
     *
     * @param passwordToCheck the password to check
     * @param userPassword the hashed password
     * @return true if the passwords match, false otherwise
     */
    private Boolean checkPassword(String passwordToCheck, String userPassword) {
        return BCrypt.checkpw(passwordToCheck, userPassword);
    }
}
