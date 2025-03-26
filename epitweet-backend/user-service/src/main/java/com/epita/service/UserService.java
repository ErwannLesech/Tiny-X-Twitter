package com.epita.service;

import com.epita.payloads.post.CreatePostRequest;
import com.epita.controller.contracts.UserRequest;
import com.epita.controller.contracts.UserResponse;
import com.epita.converter.CreatePostConverter;
import com.epita.converter.UserConverter;
import com.epita.repository.UserRepository;
import com.epita.repository.entity.User;
import com.epita.repository.publisher.CreatePostPublisher;
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
    CreatePostPublisher createPostPublisher;
    @Inject
    Logger logger;

    public UserResponse getUser(String userTag)
    {
        User user = userRepository.findByTag(userTag);
        if (user == null) {
            return null;
        }

        return new UserResponse(user._id, user.tag, user.pseudo, user.password, user.blockedUsers);
    }

    public UserResponse createUser(final UserRequest userRequest) {
        if (userRepository.findByTag(userRequest.getTag()) == null) {

            // password hash handling
            if (userRequest.getPassword() != null) {
                userRequest.setPassword(hashPassword(userRequest.getPassword()));
            }

            User newUser = UserConverter.toEntity(userRequest);
            userRepository.createUser(newUser);

            return UserConverter.toResponse(newUser);
        }

        return null;
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

    public UserResponse deleteUser(final String userTag) {
        User userToDelete = userRepository.findByTag(userTag);
        if (userToDelete != null) {
            userRepository.deleteUser(userToDelete);
            return new UserResponse(userToDelete._id,userToDelete.tag, userToDelete.pseudo, userToDelete.password, userToDelete.blockedUsers);
        }

        return null;
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

    /**
     * This function check if the userId and the parentId blocked themselves
     * @param message the CreatePostRequest from repo-post
     */
    public void createPostRequest(CreatePostRequest message) {
        logger.infof("preparing pusblishing AAAAAAAAAAAAAA");
        ObjectId userId = message.userId;
        ObjectId parentId = message.parentId;

        User user = userRepository.findById(userId);
        User parentUser = userRepository.findById(parentId);

        if (user == null || parentUser == null) {
            createPostPublisher.publish(CreatePostConverter.toCreatePostResponse(message, false, false));
        }
        else {
            Boolean childBlockedParentUser = user.blockedUsers.contains(parentId);
            Boolean parentUserBlockedUser = parentUser.blockedUsers.contains(userId);

            logger.infof("preparing pusblishing AAAAAAAAAAAAAA");

            createPostPublisher.publish(CreatePostConverter.toCreatePostResponse(message, parentUserBlockedUser, childBlockedParentUser));
        }
    }

}
