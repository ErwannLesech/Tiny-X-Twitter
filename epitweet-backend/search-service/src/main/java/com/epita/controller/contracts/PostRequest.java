package com.epita.controller.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PostRequest {
    public String id;
    public String postType;
    public String content;
    public String mediaPath;
    public String parentId;

    public PostRequest(CreatePostResponse createPostResponse) {
        this.postType = createPostResponse.postType;
        this.content = createPostResponse.content;
        this.mediaPath = createPostResponse.mediaPath;
        this.parentId = String.valueOf(createPostResponse.parentId);
    }

    public ObjectId getParentObjectId() {
        return (parentId != null && ObjectId.isValid(parentId)) ? new ObjectId(parentId) : null;
    }
}
