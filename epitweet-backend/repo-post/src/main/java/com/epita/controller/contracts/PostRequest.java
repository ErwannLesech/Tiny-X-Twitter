package com.epita.controller.contracts;

import com.epita.controller.subscriber.contracts.CreatePostResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PostRequest {
    public String postType;
    public String content;
    public String mediaUrl;
    public ObjectId parentId;

    public PostRequest(CreatePostResponse createPostResponse) {
        this.postType = createPostResponse.postType;
        this.content = createPostResponse.content;
        this.mediaUrl = createPostResponse.mediaUrl;
        this.parentId = createPostResponse.parentId;
    }
}
