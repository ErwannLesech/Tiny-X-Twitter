package com.epita.repository.entity;

import com.epita.controller.contracts.UserRequest;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection="Users")
public class User {
    public ObjectId _id;
    public String tag;
    public String pseudo;
    public List<ObjectId> blockedUsers;

    public User(UserRequest userRequest) {
        this.tag = userRequest.getTag();
        this.pseudo = userRequest.getPseudo();
        this.blockedUsers = userRequest.getBlockedUsers() != null ? userRequest.getBlockedUsers() : new ArrayList<>();
    }
}
