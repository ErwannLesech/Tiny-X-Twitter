package com.epita.controller.contracts;

import com.epita.repository.entity.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class PostResponse {
    public ObjectId _id;
    public ObjectId userId;
    public String postType;
    public String content;
    public String mediaUrl;
    public ObjectId parentId;
    public Instant createdAt;
    public Instant updatedAt;

    public PostResponse(Post post) {
        this._id = post._id;
        this.userId = post.userId;
        this.postType = post.postType.toString();
        this.content = post.content;
        this.mediaUrl = post.mediaUrl;
        this.parentId = post.parentId;
        this.createdAt = post.createdAt;
        this.updatedAt = post.updatedAt;
    }
}
