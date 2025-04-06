package com.epita.controller;

import com.epita.controller.contracts.AppreciationRequest;
import com.epita.controller.contracts.BlockUnblockRequest;
import com.epita.controller.contracts.FollowUnfollowRequest;
import com.epita.service.SocialService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/api/social")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SocialController {
    @Inject
    SocialService socialService;

    @Inject
    Logger logger;

    /**
     * Creates or updates the follow relation between two users.
     * @param followUnfollowRequest the request indicating who follows or unfollows whom
     * @return 200 if successful, 400 otherwise
     */
    @POST
    @Path("/follow")
    public Response followUnfollow(FollowUnfollowRequest followUnfollowRequest) {
        if (followUnfollowRequest == null) {
            logger.warnf("Follow/UnFollow response 400 - Empty body request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String typeRequest = followUnfollowRequest.followUnfollow ? "Follow" : "Unfollow";
        logger.infof("%s Request: %s -> %s", typeRequest, followUnfollowRequest.userFollowId,
            followUnfollowRequest.userFollowedId);

        if (
            !socialService.checkObjectId(followUnfollowRequest.userFollowedId) ||
            !socialService.checkObjectId(followUnfollowRequest.userFollowId)
        ) {
            logger.warnf("%s response 400 - Invalid request", typeRequest);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (!socialService.followUnfollow(followUnfollowRequest)) {
            logger.warnf("%s response 404 - Users not found during request", typeRequest);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        logger.infof("%s response 200 - Request done successfully", typeRequest);
        return Response.ok().build();
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
            logger.warnf("GetFollows response 400 - Empty body request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        logger.infof("GetFollows Request of user: %s", userId);

        if (!socialService.checkObjectId(userId) ||
            !socialService.checkObjectId(userId)
        ) {
            logger.warnf("GetFollows response 400 - Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<String> follows = socialService.getFollows(userId);
        if (follows == null) {
            logger.warnf("GetFollows response 404 - Users not found during request");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        logger.infof("GetFollows response 200 - Request done successfully");
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
            logger.warnf("GetFollowers response 400 - Empty body request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        logger.infof("GetFollowers Request of user: %s", userId);

        if (!socialService.checkObjectId(userId) ||
            !socialService.checkObjectId(userId)
        ) {
            logger.warnf("GetFollowers response 400 - Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<String> followers = socialService.getFollowers(userId);
        if (followers == null) {
            logger.warnf("GetFollowers response 404 - Users not found during request");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        logger.infof("GetFollowers response 200 - Request done successfully");
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
            logger.warnf("Like/UnFollow response 400 - Empty body request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String typeRequest = blockUnblockRequest.blockUnblock ? "Block" : "Unblock";
        logger.infof("%s Request: %s -> %s", typeRequest, blockUnblockRequest.userBlockId,
            blockUnblockRequest.userBlockedId);

        if (!socialService.checkObjectId(blockUnblockRequest.userBlockedId) ||
            !socialService.checkObjectId(blockUnblockRequest.userBlockId)
        ) {
            logger.warnf("Block/UnBlock response 400 - Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (!socialService.blockUnblock(blockUnblockRequest)) {
            logger.warnf("%s response 404 - Users not found during request", typeRequest);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        logger.infof("%s response 200 - Request done successfully", typeRequest);
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
        if (userId == null) {
            logger.warnf("GetBlocked response 400 - Empty body request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        logger.infof("GetBlocked Request of user: %s", userId);

        if (!socialService.checkObjectId(userId) ||
            !socialService.checkObjectId(userId)
        ) {
            logger.warnf("GetBlocked response 400 - Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<String> blockedUsers = socialService.getBlockedUsers(userId);
        if (blockedUsers == null) {
            logger.warnf("GetBlocked response 404 - Users not found during request");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        logger.infof("GetBlocked response 200 - Request done successfully");
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
            logger.warnf("GetBlock response 400 - Empty body request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        logger.infof("GetBlock Request of user: %s", userId);

        if (!socialService.checkObjectId(userId) ||
            !socialService.checkObjectId(userId)
        ) {
            logger.warnf("GetBlock response 400 - Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<String> usersWhoBlocked = socialService.getUsersWhoBlocked(userId);
        if (usersWhoBlocked == null) {
            logger.warnf("GetBlock response 404 - Users not found during request");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        logger.infof("GetBlock response 200 - Request done successfully");
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
            logger.warnf("Like/UnFollow response 400 - Empty body request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String typeRequest = request.likeUnlike ? "Like" : "Unlike";
        logger.infof("%s Request: %s -> %s", typeRequest, request.userId,
            request.postId);

        if (!socialService.checkObjectId(request.postId) ||
            !socialService.checkObjectId(request.userId)
        ) {
            logger.warnf("Like/UnFollow response 400 - Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (!socialService.likeUnlike(request)) {
            logger.warnf("%s response 404 - User or Post not found during request", typeRequest);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        logger.infof("%s response 200 - Request done successfully", typeRequest);
        return Response.ok().build();
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
            logger.warnf("GetLikeUsers response 400 - Empty body request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        logger.infof("GetLikeUsers Request of post: %s", postId);

        if (!socialService.checkObjectId(postId) ||
            !socialService.checkObjectId(postId)
        ) {
            logger.warnf("GetLikeUsers response 400 - Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<String> users = socialService.getLikeUsers(postId);
        if (users == null) {
            logger.warnf("GetLikeUsers response 404 - Users not found during request");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        logger.infof("GetLikeUsers response 200 - Request done successfully");
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
            logger.warnf("GetLikedPosts response 400 - Empty body request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        logger.infof("GetLikedPosts Request of user: %s", userId);

        if (!socialService.checkObjectId(userId) ||
            !socialService.checkObjectId(userId)
        ) {
            logger.warnf("GetLikedPosts response 400 - Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<String> likedPosts = socialService.getLikesPosts(userId);
        if (likedPosts == null) {
            logger.warnf("GetLikedPosts response 404 - Users not found during request");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        logger.infof("GetLikedPosts response 200 - Request done successfully");
        return Response.ok(likedPosts).build();
    }
}
