package com.epita.service;

import com.epita.controller.contracts.UserRequest;
import com.epita.repository.UserRepository;
import com.epita.repository.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    public Boolean createUser(final UserRequest userRequest) {
        if (userRepository.findByTag(userRequest.getTag()) == null) {
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
}
