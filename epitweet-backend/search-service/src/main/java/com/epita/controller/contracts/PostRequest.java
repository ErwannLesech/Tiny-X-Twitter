package com.epita.controller.contracts;

import com.epita.payloads.post.CreatePostResponse;
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
        this.postType = createPostResponse.getPostType();
        this.content = createPostResponse.getContent();
        this.mediaPath = createPostResponse.getMediaUrl();
        this.parentId = String.valueOf(createPostResponse.getParentId());
    }

    public ObjectId getParentObjectId() {
        return (parentId != null && ObjectId.isValid(parentId)) ? new ObjectId(parentId) : null;
    }
}
