package com.epita.controller;

import com.epita.controller.contracts.PostRequest;
import com.epita.contracts.post.PostResponse;
import com.epita.service.PostService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Objects;

/**
 * REST controller for managing posts.
 * Provides endpoints for retrieving, creating, and deleting posts.
 */
@ApplicationScoped
@Path("/api/posts/")
public class PostController {

    @Inject
    Logger logger;

    @Inject
    PostService postService;

    /**
     * Retrieves all posts for a given user.
     * @param userId User ID
     * @return A list of posts or a 400 error if userId is invalid.
     */
    @GET
    @Path("/getPosts")
    public Response getPosts(@HeaderParam("userId") ObjectId userId) {
        logger.infof("getPosts Request: %s", userId);
        if (userId == null || userId.toString().isEmpty()) {
            logger.warn("getPosts response 400 - userId is null");
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }


        List<PostResponse> posts = postService.getPosts(userId);
        logger.debugf("getPosts response 200: %s", posts);

        return Response.ok(posts).build(); // 200
    }

    /**
     * Retrieves a specific post by its ID.
     * @param postId Post ID
     * @return The post or a 404 error if not found.
     */
    @GET
    @Path("/getPost/{postId}")
    public Response getPost(@PathParam("postId") ObjectId postId) {
        logger.infof("getPost Request: %s", postId);
        if (postId == null || postId.toString().isEmpty()) {
            logger.warn("getPost response 400 - postId is null");
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        logger.infof("Fetching post with ID: %s", postId);
        PostResponse post = postService.getPost(postId);

        if (post == null) {
            logger.warnf("getPost response 404 - Post not found for ID: %s", postId);
            return Response.status(Response.Status.NOT_FOUND).build(); // 404
        }

        logger.debugf("getPost response 200: %s", post);
        return Response.ok(post).build(); // 200
    }

    /**
     * Retrieves a reply to a post.
     * @param replyPostId Reply post ID
     * @return The reply post or a 404 error if not found.
     */
    @GET
    @Path("/getPostReply/{replyPostId}")
    public Response getPostReply(@PathParam("replyPostId") ObjectId replyPostId) {
        logger.infof("getPostReply Request: %s", replyPostId);
        if (replyPostId == null || replyPostId.toString().isEmpty()) {
            logger.warn("getPostReply response 400 - replyPostId is null");
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        logger.infof("Fetching reply post with ID: %s", replyPostId);
        PostResponse post = postService.getReplyPost(replyPostId);

        if (post == null) {
            logger.warnf("getPostReply response 404 - Reply post not found for ID: %s", replyPostId);
            return Response.status(Response.Status.NOT_FOUND).build(); // 404
        }

        logger.debugf("getPostReply response 200: %s", post);
        return Response.ok(post).build(); // 200
    }

    /**
     * Creates a new post.
     * @param userId User ID creating the post
     * @param postRequest Object containing post-details
     * @return The created post (201) or an accepted response for processing (202).
     */
    @POST
    @Path("/createPost")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPost(@HeaderParam("userId") ObjectId userId, PostRequest postRequest) {
        logger.infof("createPost Request: userId=%s, postRequest=%s", userId, postRequest);
        if (userId == null || userId.toString().isEmpty() || !isRequestValid(postRequest)) {
            logger.warnf("createPost response 400 - Invalid request: userId=%s, postRequest=%s", userId, postRequest);
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        logger.infof("Creating post for userId: %s with request: %s", userId, postRequest);
        PostResponse postResponse = postService.createPostRequest(userId, postRequest);

        if (Objects.equals(postRequest.getPostType(), "post") && postResponse != null) {
            logger.infof("createPost response 201 - Post created successfully: %s", postResponse);
            return Response.status(Response.Status.CREATED).entity(postResponse).build(); // 201
        } else {
            logger.info("createPost response 202 - Post creation request accepted for processing.");
            return Response.status(Response.Status.ACCEPTED).build(); // 202
        }
    }

    /**
     * Deletes a specific post by its ID.
     * @param postId Post ID to be deleted
     * @return The deleted post or a 404 error if not found.
     */
    @DELETE
    @Path("/deletePost/{postId}")
    public Response deletePost(@PathParam("postId") ObjectId postId) {
        logger.infof("deletePost Request: %s", postId);
        if (postId == null || postId.toString().isEmpty()) {
            logger.warn("deletePost response 400 - postId is null");
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        logger.infof("Deleting post with ID: %s", postId);
        PostResponse deletedPost = postService.deletePost(postId);

        if (deletedPost == null) {
            logger.warnf("deletePost response 404 - Post not found for deletion with ID: %s", postId);
            return Response.status(Response.Status.NOT_FOUND).build(); // 404
        }

        logger.infof("deletePost response 200 - Post deleted successfully: %s", deletedPost);
        return Response.ok(deletedPost).build(); // 200
    }

    private Boolean isRequestValid(final PostRequest postRequest) {
        if (postRequest == null) {
            return Boolean.FALSE;
        }

        String postType = postRequest.getPostType();
        String content = postRequest.getContent();
        String mediaUrl = postRequest.mediaUrl;
        String parentId = postRequest.parentId;

        if (postType == null || postType.isEmpty()) {
            return Boolean.FALSE;
        }

        if (!postType.equals("post") && parentId == null) // cannot reply or repost without parentId
        {
            return Boolean.FALSE;
        }

        // check if the postRequest contains at least one of (content, mediaUrl, parentId) and at most two
        int nullFields = 0;

        if (content == null || content.isEmpty())
            nullFields++;
        else if (content.length() > 160) // check the length of content
            return Boolean.FALSE;

        if (mediaUrl == null || mediaUrl.isEmpty())
            nullFields++;

        if (parentId == null || parentId.isEmpty())
            nullFields++;

        if (nullFields < 1 || nullFields > 2)
            return Boolean.FALSE;

        return Boolean.TRUE;
    }
}
