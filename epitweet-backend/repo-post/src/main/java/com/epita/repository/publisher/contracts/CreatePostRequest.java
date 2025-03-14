package com.epita.repository.publisher.contracts;

import com.epita.controller.contracts.PostRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostRequest {
    public ObjectId userId;
    public String postType;
    public String content;
    public String mediaUrl;
    public ObjectId parentId;

    public CreatePostRequest(ObjectId userId, PostRequest postRequest) {
        this.userId = userId;
        this.postType = postRequest.getPostType();
        this.content = postRequest.getContent();
        this.mediaUrl = postRequest.getMediaPath();
        this.parentId = postRequest.getParentId();
    }
}
