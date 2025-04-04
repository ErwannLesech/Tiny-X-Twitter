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
        socialService.followUnfollow(followUnfollowRequest);
        return Response.ok().build(); // 200 OK
    }

    /**
     * Gets the users followed by a specific user.
     * @param userId the user for whom to get the followed users
     * @return a Response containing a list of userIds who are followed by the specified userId
     */
    @GET
    @Path("/getFollows/{userId}")
    public Response getFollows(@PathParam("userId") String userId) {
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
        socialService.blockUnblock(blockUnblockRequest);
        return Response.ok().build();
    }

    /**
     * Gets the users blocked by a specific user.
     * @param userId the user for whom to get the blocked users
     * @return a Response containing a list of userIds who are blocked by the specified userId
     */
    @GET
    @Path("/getBlocked/{userId}")
    public Response getBlocked(@PathParam("userId") String userId) {
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
        List<String> usersWhoBlocked = socialService.getUsersWhoBlocked(userId);
        if (usersWhoBlocked == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(usersWhoBlocked).build();
    }

    @POST
    @Path("/like")
    public Response likeUnlike(AppreciationRequest request) {
        socialService.likeUnlike(request);
        return Response.ok().build();
    }

    @GET
    @Path("/getLikeUsers/{postId}")
    public Response getLikeUsers(@PathParam("postId") String postId) {
        List<String> users = socialService.getLikeUsers(postId);
        if (users == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(users).build();
    }

    @GET
    @Path("/getLikedPosts/{userId}")
    public Response getLikedPosts(@PathParam("userId") String userId) {
        List<String> likedPosts = socialService.getLikesPosts(userId);
        if (likedPosts == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(likedPosts).build();
    }
}
