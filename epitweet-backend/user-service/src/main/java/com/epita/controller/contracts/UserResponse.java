package com.epita.controller.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserResponse {
    ObjectId _id;
    String tag;
    String pseudo;
    String password;
    List<ObjectId> blockedUsers;
}
