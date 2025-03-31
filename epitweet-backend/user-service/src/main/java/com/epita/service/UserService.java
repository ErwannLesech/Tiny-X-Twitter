package com.epita.service;

import com.epita.controller.contracts.UserRequest;
import com.epita.controller.contracts.UserResponse;
import com.epita.converter.UserConverter;
import com.epita.repository.UserRepository;
import com.epita.repository.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    /**
     * Retrieves a user by their tag.
     *
     * @param userTag the tag of the user
     * @return a UserResponse object if the user is found, or null
     */
    public UserResponse getUser(String userTag) {
        User user = userRepository.findByTag(userTag);
        if (user == null) {
            return null;
        }

        return new UserResponse(user._id, user.tag, user.pseudo, user.password, user.blockedUsers);
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
    public Boolean updateUser(final UserRequest userRequest) {
        User userToUpdate = userRepository.findByTag(userRequest.getTag());
        if (userToUpdate != null) {
            userToUpdate.pseudo = userRequest.getPseudo();
            userToUpdate.blockedUsers = userRequest.getBlockedUsers();

            // Password hash handling
            if (userRequest.getPassword() != null) {
                userToUpdate.password = hashPassword(userRequest.getPassword());
            }

            userRepository.updateUser(userToUpdate);
            return true;
        }

        return false;
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

        if (checkPassword(userRequest.getPassword(), user.password)) {
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
