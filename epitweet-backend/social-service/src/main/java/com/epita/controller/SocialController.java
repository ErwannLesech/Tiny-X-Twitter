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

    @POST
    @Path("/follow")
    public Response followUnfollow(FollowUnfollowRequest followUnfollowRequest) {
        socialService.followUnfollow(followUnfollowRequest);
        return Response.ok().build(); // 200 OK
    }

    @GET
    @Path("/getFollows/{userId}")
    public Response getFollows(@PathParam("userId") String userId) {
        List<String> follows = socialService.getFollows(userId);
        if (follows == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(follows).build();
    }

    @GET
    @Path("/getFollowers/{userId}")
    public Response getFollowers(@PathParam("userId") String userId) {
        List<String> followers = socialService.getFollowers(userId);
        if (followers == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(followers).build();
    }

    @POST
    @Path("/block")
    public Response blockUnblock(BlockUnblockRequest request) {
        socialService.blockUnblock(request);
        return Response.ok().build();
    }

    @GET
    @Path("/getBlocked/{userId}")
    public Response getBlocked(@PathParam("userId") String userId) {
        List<String> blockedUsers = socialService.getBlockedUsers(userId);
        if (blockedUsers == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(blockedUsers).build();
    }

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
