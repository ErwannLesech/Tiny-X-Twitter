package com.epita.converter;

import com.epita.controller.contracts.PostRequest;
import com.epita.contracts.post.PostResponse;
import com.epita.payloads.post.CreatePostRequest;
import com.epita.payloads.post.CreatePostResponse;
import com.epita.payloads.search.IndexPost;
import com.epita.repository.entity.Post;
import com.epita.repository.entity.PostType;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

/**
 * Utility class for converting between different representations of a Post.
 */
public class PostConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostConverter.class);

    /**
     * Converts a {@code PostRequest} to a {@code Post} entity.
     *
     * @param userId      The ID of the user creating the post.
     * @param postRequest The {@code PostRequest} to convert.
     * @return The converted {@code Post} entity.
     */
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

    /**
     * Converts a {@code Post} entity to a {@code PostResponse}.
     *
     * @param post The {@code Post} entity to convert.
     * @return The converted {@code PostResponse}.
     */
    public static PostResponse toResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getUserId(),
                post.getPostType().toString(),
                post.getContent(),
                post.getMediaUrl(),
                post.getParentId(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    /**
     * Converts a {@code CreatePostResponse} to a {@code PostRequest}.
     *
     * @param createPostResponse The {@code CreatePostResponse} to convert.
     * @return The converted {@code PostRequest}.
     */
    public static PostRequest toRequest(CreatePostResponse createPostResponse) {
        return new PostRequest(
                createPostResponse.getPostType(),
                createPostResponse.getContent(),
                createPostResponse.getMediaUrl(),
                createPostResponse.getParentId() != null ? createPostResponse.getParentId().toString() : null
        );
    }

    /**
     * Converts a {@code PostRequest} to a {@code CreatePostRequest}.
     *
     * @param userId      The ID of the user creating the post.
     * @param postRequest The {@code PostRequest} to convert.
     * @return The converted {@code CreatePostRequest}.
     */
    public static CreatePostRequest toCreatePostRequest(ObjectId userId, PostRequest postRequest) {
        return new CreatePostRequest(
                userId,
                postRequest.getPostType(),
                postRequest.getContent(),
                postRequest.getMediaUrl(),
                postRequest.getParentObjectId()
        );
    }

    /**
     * Converts a {@code Post} to a {@code IndexPost}.
     *
     * @param post   The post that has been created or deleted
     * @param method The method (creation or deletion)
     * @return The converted {@code IndexPost}.
     */
    public static IndexPost toIndexPost(Post post, String method){
        return new IndexPost(
                post.getId().toString(),
                post.getPostType().toString(),
                post.getContent(),
                post.getMediaUrl(),
                post.getParentId(),
                method
        );
    }
}
