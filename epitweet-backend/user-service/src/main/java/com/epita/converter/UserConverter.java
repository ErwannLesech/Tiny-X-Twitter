package com.epita.converter;

import com.epita.controller.contracts.UserRequest;
import com.epita.controller.contracts.UserResponse;
import com.epita.repository.entity.User;
import lombok.*;
import org.bson.types.ObjectId;
import java.util.ArrayList;

public class UserConverter {

    public static User toEntity(UserRequest userRequest) {
        return new User(
                new ObjectId(),
                userRequest.getTag(),
                userRequest.getPseudo(),
                userRequest.getPassword(),
                userRequest.getBlockedUsers() != null ? userRequest.getBlockedUsers() : new ArrayList<>()
        );
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user._id,
                user.tag,
                user.pseudo,
                user.password,
                user.blockedUsers
        );
    }
}
