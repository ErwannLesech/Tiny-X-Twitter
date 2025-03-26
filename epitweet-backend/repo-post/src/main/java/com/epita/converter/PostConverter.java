package com.epita.converter;

import com.epita.controller.contracts.PostRequest;
import com.epita.controller.contracts.PostResponse;
import com.epita.controller.subscriber.contracts.CreatePostResponse;
import com.epita.repository.entity.Post;
import com.epita.repository.entity.PostType;
import com.epita.repository.publisher.contracts.CreatePostRequest;
import org.bson.types.ObjectId;

import java.time.Instant;

public class PostConverter {

    public static Post toEntity(ObjectId userId, PostRequest postRequest) {
        return new Post(
                userId,
                PostType.fromString(postRequest.getPostType()),
                postRequest.getContent(),
                postRequest.getMediaUrl(),
                postRequest.getParentObjectId(),
                Instant.now(),
                Instant.now()
        );
    }

    public static PostResponse toResponse(Post post) {
        return new PostResponse(
                post._id,
                post.userId,
                post.postType.toString(),
                post.content,
                post.mediaUrl,
                post.parentId,
                post.createdAt,
                post.updatedAt
        );
    }

    public static PostRequest toRequest(CreatePostResponse createPostResponse) {
        return new PostRequest(
                createPostResponse.getPostType(),
                createPostResponse.getContent(),
                createPostResponse.getMediaUrl(),
                createPostResponse.getParentId() != null ? createPostResponse.getParentId().toString() : null
        );
    }

    public static CreatePostRequest toCreatePostRequest(ObjectId userId, PostRequest postRequest) {
        return new CreatePostRequest(
                userId,
                postRequest.getPostType(),
                postRequest.getContent(),
                postRequest.getMediaUrl(),
                postRequest.getParentObjectId()
        );
    }
}
