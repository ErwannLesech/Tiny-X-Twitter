package com.epita.controller.contracts;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
@ToString
public class UserRequest {
    String tag;
    String pseudo;
    String password;
    List<ObjectId> blockedUsers;
}
