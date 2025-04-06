package com.epita.service;

import com.epita.contracts.social.BlockedRelationRequest;
import com.epita.contracts.social.BlockedRelationResponse;
import com.epita.contracts.user.UserResponse;
import com.epita.controller.contracts.PostRequest;
import com.epita.converter.PostTimelineConverter;
import com.epita.contracts.post.PostResponse;
import com.epita.converter.PostConverter;
import com.epita.repository.restClient.SocialRestClient;
import com.epita.repository.restClient.UserRestClient;
import com.epita.repository.PostRepository;
import com.epita.repository.entity.Post;
import com.epita.repository.entity.PostType;
import com.epita.repository.publisher.PostHomeTimelinePublisher;
import com.epita.repository.publisher.PostTimelinePublisher;
import com.epita.repository.publisher.IndexPostPublisher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.Instant;
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
    PostTimelinePublisher postTimelinePublisher;

    @Inject
    PostHomeTimelinePublisher postHomeTimelinePublisher;

    @Inject
    IndexPostPublisher indexPostPublisher;

    @Inject
    @RestClient
    UserRestClient userRestClient;

    @Inject
    @RestClient
    SocialRestClient socialRestClient;

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
     * Retrieves list of reply posts by its ID.
     *
     * @param replyPostId the ID of the reply post
     * @return a PostResponse object or null if the post is not found or is not a reply
     */
    public List<PostResponse> getRepliesPost(ObjectId replyPostId) {
        Post parentPost = postRepository.findById(replyPostId);

        if (parentPost == null) {
            return null;
        }

        // check all replies of this post
        List<Post> replies = postRepository.findByParentId(parentPost.getId());
        List<PostResponse> repliesResponses = new ArrayList<>();
        for (Post post : replies) {
            if (post.getPostType() == PostType.REPLY) {
                repliesResponses.add(PostConverter.toResponse(post));
            }
        }

        return repliesResponses;
    }

    /**
     * Retrieves a list of repost posts.
     *
     * @param postReferenceId the ID of the reply post
     * @return a PostResponse object or null if the post is not found or is not a reply
     */
    public List<PostResponse> getRepostsPost(ObjectId postReferenceId) {
        Post parentPost = postRepository.findById(postReferenceId);
        if (parentPost == null) {
            return null;
        }

        List<Post> replies = postRepository.findByParentId(parentPost.getId());
        List<PostResponse> repliesResponses = new ArrayList<>();
        for (Post post : replies) {
            if (post.getPostType() == PostType.REPOST) {
                repliesResponses.add(PostConverter.toResponse(post));
            }
        }

        return repliesResponses;
    }

    /**
     * Retrieves a list of repost posts and replies.
     *
     * @param postReferenceId the ID of the reply post
     * @return a PostResponse object or null if the post is not found or is not a reply
     */
    public List<PostResponse> getRepostsAndRepliesPost(ObjectId postReferenceId) {
        Post parentPost = postRepository.findById(postReferenceId);
        if (parentPost == null) {
            return null;
        }

        List<Post> replies = postRepository.findByParentId(parentPost.getId());
        List<PostResponse> repliesResponses = new ArrayList<>();
        for (Post post : replies) {
            repliesResponses.add(PostConverter.toResponse(post));
        }

        return repliesResponses;
    }

    /**
     * Creates a new post-request.
     *
     * @param userId the ID of the user creating the post
     * @param postRequest the post-request details
     * @return a PostResponse object or null if the post-type is not POST
     */
    public PostResponse createPostRequest(ObjectId userId, PostRequest postRequest) {
        // Check if user exists
        try (RestResponse<UserResponse> response = userRestClient.getUserById(userId)) {
            if (response == null || response.getStatus() != 200) {
                return null;
            }
        } catch (ClientWebApplicationException e) {
            return null;
        }

        if (!Objects.equals(postRequest.postType, PostType.POST.toString())) {
            // Check if parentId or user have not blocked themselves
            ObjectId parentUserId = postRepository.findById(postRequest.getParentObjectId()).getUserId();
            BlockedRelationRequest blockedRelationRequest =
                    PostConverter.toBlockedRelationRequest(userId, parentUserId);

            BlockedRelationResponse blockedRelationResponse;
            try {
                blockedRelationResponse = socialRestClient.getBlockedRelation(blockedRelationRequest).getEntity();
            } catch (ClientWebApplicationException e) {
                return null;
            }

            if (blockedRelationResponse == null) {
                return null;
            }

            // check if non block
            if  (blockedRelationResponse.getParentUserBlockedUser() ||
                    blockedRelationResponse.getUserBlockedParentUser()){
                return null;
            }
        }

        Post createdPost = createPost(userId, postRequest);
        return PostConverter.toResponse(createdPost);
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

        // declare to user-timeline that we created a post
        postTimelinePublisher.publish(PostTimelineConverter.toPostTimeline(post, "creation"));

        // declare to home-timeline that we deleted a post
        postHomeTimelinePublisher.publish(PostTimelineConverter.toPostHomeTimeline(PostConverter.toResponse(post),
                "deletion"));

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

        post.setUpdatedAt(Instant.now());

        PostResponse postResponse = PostConverter.toResponse(post);

        postRepository.deletePost(post);

        // declare to user-timeline that we deleted a post
        postTimelinePublisher.publish(PostTimelineConverter.toPostTimeline(post, "deletion"));

        // declare to home-timeline that we deleted a post
        postHomeTimelinePublisher.publish(PostTimelineConverter.toPostHomeTimeline(PostConverter.toResponse(post),
                "deletion"));

        // declare to index service that we deleted a Post
        indexPostPublisher.publish(PostConverter.toIndexPost(post, "deletion"));

        return postResponse;
    }

    /**
     * Deletes all posts of a userId
     *
     * @param userId the ID of the user
     */
    public void deleteUserPost(ObjectId userId) {
        List<Post> posts = postRepository.findByUser(userId);

        for (Post post : posts) {
            postRepository.deletePost(post);
        }
    }
}
