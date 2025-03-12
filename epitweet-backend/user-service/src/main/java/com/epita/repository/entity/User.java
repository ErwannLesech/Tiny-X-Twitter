package com.epita.repository.entity;

import com.epita.controller.contracts.UserRequest;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;

import java.util.List;

@MongoEntity(collection="Users")
public class User {
    public ObjectId _id;
    public String tag;
    public String pseudo;
    public List<ObjectId> blockedUsers;

    public User(UserRequest userRequest) {
        this.tag = userRequest.getTag();
        this.pseudo = userRequest.getPseudo();
        this.blockedUsers = userRequest.getBlockedUsers();
    }
}
