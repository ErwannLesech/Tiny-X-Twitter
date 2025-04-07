package com.epita.payloads.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DeleteUserPost {
    ObjectId userId;
}
