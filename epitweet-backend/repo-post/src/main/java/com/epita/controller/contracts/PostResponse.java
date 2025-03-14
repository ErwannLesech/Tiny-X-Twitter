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
public class PostResponse {
    public ObjectId postId;
    public ObjectId userId;
    public String postType;
    public String content;
    public String mediaPath;
    public ObjectId parentId;
}
