package com.epita.converter;

import com.epita.controller.contracts.PostRequest;
import com.epita.controller.contracts.PostResponse;
import com.epita.payloads.post.CreatePostRequest;
import com.epita.payloads.post.CreatePostResponse;
import com.epita.repository.entity.Post;
import com.epita.repository.entity.PostType;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

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
        LOGGER.debug("Converting PostRequest to Post entity for userId: {}", userId);
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
        LOGGER.debug("Converting Post entity to PostResponse for postId: {}", post._id);
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

    /**
     * Converts a {@code CreatePostResponse} to a {@code PostRequest}.
     *
     * @param createPostResponse The {@code CreatePostResponse} to convert.
     * @return The converted {@code PostRequest}.
     */
    public static PostRequest toRequest(CreatePostResponse createPostResponse) {
        LOGGER.debug("Converting CreatePostResponse to PostRequest");
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
        LOGGER.debug("Converting PostRequest to CreatePostRequest for userId: {}", userId);
        return new CreatePostRequest(
                userId,
                postRequest.getPostType(),
                postRequest.getContent(),
                postRequest.getMediaUrl(),
                postRequest.getParentObjectId()
        );
    }
}
