package com.epita.service;

import com.epita.controller.contracts.UserRequest;
import com.epita.controller.contracts.UserResponse;
import com.epita.repository.UserRepository;
import com.epita.repository.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    public UserResponse getUser(String userTag)
    {
        User user = userRepository.findByTag(userTag);
        if (user == null) {
            return null;
        }

        return new UserResponse(user._id, user.tag, user.pseudo, user.password, user.blockedUsers);
    }

    public Boolean createUser(final UserRequest userRequest) {
        if (userRepository.findByTag(userRequest.getTag()) == null) {

            // password hash handling
            if (userRequest.getPassword() != null) {
                userRequest.setPassword(hashPassword(userRequest.getPassword()));
            }

            User newUser = new User(userRequest);
            userRepository.createUser(newUser);
            return true;
        }

        return false;
    }

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

    public Boolean deleteUser(final String userTag) {
        User userToDelete = userRepository.findByTag(userTag);
        if (userToDelete != null) {
            userRepository.deleteUser(userToDelete);
            return true;
        }

        return false;
    }

    /***
     * Fonction to get access if tag and passowrd valids
     * @param userRequest the body of the request
     * @return 404 if user unknown, 401 if password doesn't match, 200 if it matches
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

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private Boolean checkPassword(String passwordToCheck, String userPassword) {
        return BCrypt.checkpw(passwordToCheck, userPassword);
    }
}
