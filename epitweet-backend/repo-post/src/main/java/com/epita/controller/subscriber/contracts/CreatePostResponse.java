package com.epita.controller.subscriber.contracts;


import com.epita.controller.contracts.PostRequest;
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
    public String mediaUrl;
    public ObjectId parentId;
    public Boolean parentUserBlockedUser;
    public Boolean userBlockedParentUser;
}

