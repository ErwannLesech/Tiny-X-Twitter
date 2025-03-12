package com.epita.controller.contracts;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
public class UserRequest {
    String tag;
    String pseudo;
    List<ObjectId> blockedUsers;
}
