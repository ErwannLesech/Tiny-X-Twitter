package com.epita.payloads.post;

import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreatePostRequest {
    public ObjectId userId;
    public String postType;
    public String content;
    public String mediaUrl;
    public ObjectId parentId;
}