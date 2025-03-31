package com.epita.service;

import com.epita.controller.contracts.PostRequest;
import com.epita.contracts.post.PostResponse;
import com.epita.payloads.post.CreatePostResponse;
import com.epita.converter.PostConverter;
import com.epita.repository.publisher.CreatePostPublisher;
import com.epita.repository.PostRepository;
import com.epita.repository.entity.Post;
import com.epita.repository.entity.PostType;
import com.epita.repository.publisher.IndexPostPublisher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service for managing Post entities.
 */
@ApplicationScoped
public class PostService {

    @Inject
    PostRepository postRepository;

    @Inject
    CreatePostPublisher createPostPublisher;

    @Inject
    IndexPostPublisher indexPostPublisher;

    /**
     * Retrieves a list of posts for a given user.
     *
     * @param userId the ID of the user
     * @return a list of PostResponse objects
     */
    public List<PostResponse> getPosts(ObjectId userId) {
        List<Post> posts = postRepository.findByUser(userId);

        List<PostResponse> postResponses = new ArrayList<>();
        for (Post post : posts) {
            postResponses.add(PostConverter.toResponse(post));
        }

        return postResponses;
    }

    /**
     * Retrieves a single post by its ID.
     *
     * @param postId the ID of the post
     * @return a PostResponse object or null if the post is not found
     */
    public PostResponse getPost(ObjectId postId) {
        Post post = postRepository.findById(postId);

        if (post == null) {
            return null;
        }

        return PostConverter.toResponse(post);
    }

    /**
     * Retrieves a reply post by its ID.
     *
     * @param replyPostId the ID of the reply post
     * @return a PostResponse object or null if the post is not found or is not a reply
     */
    public PostResponse getReplyPost(ObjectId replyPostId) {
        Post post = postRepository.findById(replyPostId);

        if (post == null || post.postType != PostType.REPLY) {
            return null;
        }

        // check if parent post still exists
        Post repliedPost = postRepository.findById(post.parentId);
        if (repliedPost == null) {
            post.parentId = null;
        }

        return PostConverter.toResponse(post);
    }

    /**
     * Creates a new post-request.
     *
     * @param userId the ID of the user creating the post
     * @param postRequest the post-request details
     * @return a PostResponse object or null if the post-type is not POST
     */
    public PostResponse createPostRequest(ObjectId userId, PostRequest postRequest) {
        if (Objects.equals(postRequest.postType, PostType.POST.toString())) {
            // Independent post, no need block check
            Post createdPost = createPost(userId, postRequest);
            return PostConverter.toResponse(createdPost);
        } else {
            createPostPublisher.publish(PostConverter.toCreatePostRequest(userId, postRequest));
            return null;
        }
    }

    /**
     * Handles the creation of a post-response.
     *
     * @param createPostResponse the creation post-response details
     */
    public void createPostResponse(CreatePostResponse createPostResponse) {
        if (createPostResponse.getParentUserBlockedUser() || createPostResponse.getUserBlockedParentUser()) {
            return;
        }

        ObjectId userId = createPostResponse.getUserId();
        PostRequest postRequest = PostConverter.toRequest(createPostResponse);

        createPost(userId, postRequest);
    }

    /**
     * Creates a new post.
     *
     * @param userId the ID of the user creating the post
     * @param postRequest the post-request details
     * @return the created Post object
     */
    public Post createPost(ObjectId userId, PostRequest postRequest) {
        Post post = PostConverter.toEntity(userId, postRequest);

        postRepository.createPost(post);

        // declare to index service that we created a Post
        indexPostPublisher.publish(PostConverter.toIndexPost(post, "creation"));

        return post;
    }

    /**
     * Deletes a post by its ID.
     *
     * @param postId the ID of the post to delete
     * @return a PostResponse object or null if the post is not found
     */
    public PostResponse deletePost(ObjectId postId) {
        Post post = postRepository.findById(postId);

        if (post == null) {
            return null;
        }

        PostResponse postResponse = PostConverter.toResponse(post);

        postRepository.deletePost(post);

        // declare to index service that we deleted a Post
        indexPostPublisher.publish(PostConverter.toIndexPost(post, "deletion"));

        return postResponse;
    }
}
