package com.epita.controller;

import com.epita.controller.contracts.PostRequest;
import com.epita.controller.contracts.PostResponse;
import com.epita.service.PostService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import java.util.List;

@Slf4j
@ApplicationScoped
@Path("/api/posts/")
public class PostController {

    @Inject
    PostController postController;

    @Inject
    Logger logger;
    @Inject
    PostService postService;

    @GET
    @Path("/getPosts")
    public Response getPosts(@HeaderParam("userId") ObjectId userId) {
        if (userId == null || userId.toString().isEmpty())
        {
            logger.warn("userId is null");
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        List<PostResponse> posts = postService.getPosts(userId);

        return Response.ok(posts).build(); // 200
    }

    @GET
    @Path("/getPost/{postId}")
    public Response getPost(@PathParam("postId") ObjectId postId) {
        if (postId == null || postId.toString().isEmpty())
        {
            logger.warn("postId is null");
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        PostResponse post = postService.getPost(postId);

        if (post == null)
            return Response.status(Response.Status.NOT_FOUND).build(); // 404

        return Response.ok(post).build(); // 200
    }

    @GET
    @Path("/getPostReply/{replyPostId}")
    public Response getPostReply(@PathParam("replyPostId") ObjectId replyPostId) {
        if (replyPostId == null || replyPostId.toString().isEmpty())
        {
            logger.warn("replyPostId is null");
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        PostResponse post = postService.getReplyPost(replyPostId);

        if (post == null)
            return Response.status(Response.Status.NOT_FOUND).build(); // 404

        return Response.ok(post).build(); // 200
    }

    @POST
    @Path("/createPost")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPost(@HeaderParam("userId") ObjectId userId, PostRequest postRequest) {
        if (userId == null || userId.toString().isEmpty() || !isRequestValid(postRequest))
        {
            logger.warnf("userId is null or request is invalid : %s", postRequest.toString());
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        postService.createPostRequest(userId, postRequest); // send redis queue for user-service

        return Response.status(Response.Status.ACCEPTED).build(); // 202 asynchronous
    }

    @DELETE
    @Path("/deletePost/{postId}")
    public Response deletePost(@PathParam("postId") ObjectId postId) {
        if (postId == null || postId.toString().isEmpty())
        {
            logger.warn("postId is null");
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        PostResponse deletedPost = postService.deletePost(postId);

        if (deletedPost == null)
            return Response.status(Response.Status.NOT_FOUND).build(); // 404

        return Response.ok(deletedPost).build(); // 200
    }

    private Boolean isRequestValid(final PostRequest postRequest) {
        if (postRequest == null) {
            return Boolean.FALSE;
        }

        String postType = postRequest.getPostType();
        String content = postRequest.getContent();
        String mediaUrl = postRequest.mediaPath;
        ObjectId parentId = postRequest.parentId;

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

        if (parentId == null || parentId.toString().isEmpty())
            nullFields++;

        if (nullFields < 1 || nullFields > 2)
            return Boolean.FALSE;

        return Boolean.TRUE;
    }
}
