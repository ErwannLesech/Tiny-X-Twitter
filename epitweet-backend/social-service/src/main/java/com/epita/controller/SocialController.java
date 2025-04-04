package com.epita.controller;

import com.epita.controller.contracts.AppreciationRequest;
import com.epita.controller.contracts.BlockUnblockRequest;
import com.epita.controller.contracts.FollowUnfollowRequest;
import com.epita.service.SocialService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/social")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SocialController {
    @Inject
    SocialService socialService;

    /**
     * Creates or updates the follow relation between two users.
     * @param followUnfollowRequest the request indicating who follows or unfollows whom
     * @return 200 if successful, 400 otherwise
     */
    @POST
    @Path("/follow")
    public Response followUnfollow(FollowUnfollowRequest followUnfollowRequest) {
        if (followUnfollowRequest == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            socialService.followUnfollow(followUnfollowRequest);
            return Response.ok().build(); // 200 OK
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Gets the users followed by a specific user.
     * @param userId the user for whom to get the followed users
     * @return a Response containing a list of userIds who are followed by the specified userId
     */
    @GET
    @Path("/getFollows/{userId}")
    public Response getFollows(@PathParam("userId") String userId) {
        if (userId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        List<String> follows = socialService.getFollows(userId);
        if (follows == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(follows).build();
    }

    /**
     * Gets the followers of a specific user.
     * @param userId the user for whom to get the followers
     * @return a Response containing a list of userIds who follow the specified userId
     */
    @GET
    @Path("/getFollowers/{userId}")
    public Response getFollowers(@PathParam("userId") String userId) {
        if (userId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        List<String> followers = socialService.getFollowers(userId);
        if (followers == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(followers).build();
    }

    /**
     * Creates or updates the block relation between two users.
     * @param blockUnblockRequest the request indicating who blocks or unblocks whom
     * @return 200 if successful, 400 otherwise
     */
    @POST
    @Path("/block")
    public Response blockUnblock(BlockUnblockRequest blockUnblockRequest) {
        if (blockUnblockRequest == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            socialService.blockUnblock(blockUnblockRequest);
            return Response.ok().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Gets the users blocked by a specific user.
     * @param userId the user for whom to get the blocked users
     * @return a Response containing a list of userIds who are blocked by the specified userId
     */
    @GET
    @Path("/getBlocked/{userId}")
    public Response getBlocked(@PathParam("userId") String userId) {
        if (userId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        List<String> blockedUsers = socialService.getBlockedUsers(userId);
        if (blockedUsers == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(blockedUsers).build();
    }

    /**
     * Gets the users who blocked a specific user.
     * @param userId the user for whom to get the users who blocked them
     * @return a Response containing a list of userIds who have blocked the specified userId
     */
    @GET
    @Path("/getBlock/{userId}")
    public Response getBlock(@PathParam("userId") String userId) {
        if (userId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        List<String> usersWhoBlocked = socialService.getUsersWhoBlocked(userId);
        if (usersWhoBlocked == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(usersWhoBlocked).build();
    }

    /**
     * Creates or updates the like relation between one user and one post.
     * @param request the request indicating who like or unlike which post
     */
    @POST
    @Path("/like")
    public Response likeUnlike(AppreciationRequest request) {
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            socialService.likeUnlike(request);
            return Response.ok().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Gets the users who liked a specific post.
     * @param postId the post for which to get the users who liked it
     * @return a Response containing a list of userIds who liked the specified post
     */
    @GET
    @Path("/getLikeUsers/{postId}")
    public Response getLikeUsers(@PathParam("postId") String postId) {
        if (postId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        List<String> users = socialService.getLikeUsers(postId);
        if (users == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(users).build();
    }

    /**
     * Gets the posts liked by a specific user.
     * @param userId the user for whom to get the posts they liked
     * @return a Response containing a list of postIds that the specified userId liked
     */
    @GET
    @Path("/getLikedPosts/{userId}")
    public Response getLikedPosts(@PathParam("userId") String userId) {
        if (userId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        List<String> likedPosts = socialService.getLikesPosts(userId);
        if (likedPosts == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(likedPosts).build();
    }
}
