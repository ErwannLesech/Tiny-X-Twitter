package com.epita.repository.entity;

import com.epita.controller.contracts.UserRequest;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@MongoEntity(collection="Users")
public class User {
    public ObjectId _id;
    public String tag;
    public String pseudo;
    public String password;
    public List<ObjectId> blockedUsers;

    public User(UserRequest userRequest) {
        this.tag = userRequest.getTag();
        this.pseudo = userRequest.getPseudo();
        this.password = userRequest.getPassword() != null ? userRequest.getPassword() : null;
        this.blockedUsers = userRequest.getBlockedUsers() != null ? userRequest.getBlockedUsers() : new ArrayList<>();
    }
}
