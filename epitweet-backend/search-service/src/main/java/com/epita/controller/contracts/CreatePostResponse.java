package com.epita.controller.contracts;

import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreatePostResponse {
    public ObjectId userId;
    public String postType;
    public String content;
    public String mediaPath;
    public ObjectId parentId;
}
