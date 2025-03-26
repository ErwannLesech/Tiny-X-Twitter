package com.epita.repository.publisher.contracts;

import com.epita.controller.subscriber.contracts.CreatePostRequest;
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

    public CreatePostResponse(CreatePostRequest message, Boolean parentUserBlockedUser, Boolean childBlockedParentUser) {
        this.userId = message.getUserId();
        this.postType = message.getPostType();
        this.content = message.getContent();
        this.mediaUrl = message.getMediaUrl();
        this.parentId = message.getParentId();
        this.parentUserBlockedUser = parentUserBlockedUser;
        this.userBlockedParentUser = parentUserBlockedUser;
    }
}

