package com.epita.service;

import com.epita.contracts.social.BlockedRelationRequest;
import com.epita.contracts.social.BlockedRelationResponse;
import com.epita.controller.contracts.AppreciationRequest;
import com.epita.controller.contracts.BlockUnblockRequest;
import com.epita.controller.contracts.FollowUnfollowRequest;
import com.epita.contracts.social.LikedPostInfo;
import com.epita.converter.SocialConverter;
import com.epita.payloads.homeTimeline.SocialHomeTimelineBlock;
import com.epita.payloads.homeTimeline.SocialHomeTimelineFollow;
import com.epita.payloads.homeTimeline.SocialHomeTimelineLike;
import com.epita.payloads.userTimeline.LikeTimeline;
import com.epita.repository.SocialRepository;
import com.epita.repository.publisher.LikeTimelinePublisher;
import com.epita.repository.publisher.SocialHomeTimelineBlockPublisher;
import com.epita.repository.publisher.SocialHomeTimelineFollowPublisher;
import com.epita.repository.publisher.SocialHomeTimelineLikePublisher;
import com.epita.repository.restClient.PostRestClient;
import com.epita.repository.restClient.UserRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import java.util.List;

@ApplicationScoped
public class SocialService {
    @Inject
    SocialRepository socialRepository;

    @Inject
    @RestClient
    UserRestClient userRestClient;

    @Inject
    @RestClient
    PostRestClient postRestClient;

    @Inject
    SocialHomeTimelineFollowPublisher socialHomeTimelineFollowPublisher;

    @Inject
    SocialHomeTimelineBlockPublisher socialHomeTimelineBlockPublisher;

    @Inject
    SocialHomeTimelineLikePublisher socialHomeTimelineLikePublisher;

    @Inject
    LikeTimelinePublisher likeTimelinePublisher;

    @Inject
    Logger logger;

    /**
     * check if a string is a valid ObjectId
     */
    public boolean checkObjectId(String id) {
        try{
            new ObjectId(id);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Creates or updates the follow relation between two users.
     * @param request the request indicating who follows or unfollows whom
     */
    public boolean followUnfollow(FollowUnfollowRequest request) {
        //check if users exist in their repo
        try {
            if (userRestClient.getUser(new ObjectId(request.userFollowId)).getStatus() !=
                Response.Status.OK.getStatusCode() ||
                userRestClient.getUser(new ObjectId(request.userFollowedId)).getStatus() !=
                    Response.Status.OK.getStatusCode()) {
                return false;
            }
        }
        catch (ClientWebApplicationException e) {
            return false;
        }
        //check if exist locally and created if needed
        if (!socialRepository.userExists(request.getUserFollowId())) {
            socialRepository.createResource(List.of(request.userFollowId), SocialRepository.TypeCreate.USER);
        }
        if (!socialRepository.userExists(request.getUserFollowedId())) {
            socialRepository.createResource(List.of(request.getUserFollowedId()), SocialRepository.TypeCreate.USER);
        }

        socialRepository.followUnfollow(request);

        SocialHomeTimelineFollow socialHomeTimelineFollow = SocialConverter.toHomeFollow(request);
        socialHomeTimelineFollowPublisher.publish(socialHomeTimelineFollow);
        return true;
    }

    /**
     * Gets the users followed by a specific user.
     * @param userId the user for whom to get the followed users
     * @return a list of userIds who are followed by the specified userId
     */
    public List<String> getFollows(String userId) {
        if (!socialRepository.userExists(userId)) {
            return null;
        }

        return socialRepository.getFollows(userId);
    }

    /**
     * Gets the followers of a specific user.
     * @param userId the user for whom to get the followers
     * @return a list of userIds who follow the specified userId
     */
    public List<String> getFollowers(String userId) {
        if (!socialRepository.userExists(userId)) {
            return null;
        }

        return socialRepository.getFollowers(userId);
    }

    /**
     * Creates or updates the block relation between two users.
     * @param request the request indicating who blocks or unblocks whom
     */
    public boolean blockUnblock(BlockUnblockRequest request) {
        //check if users exist in their repo
        try {
            if (userRestClient.getUser(new ObjectId(request.userBlockedId)).getStatus() !=
                Response.Status.OK.getStatusCode() ||
                userRestClient.getUser(new ObjectId(request.userBlockId)).getStatus() !=
                    Response.Status.OK.getStatusCode()) {
                return false;
            }
        }
        catch (ClientWebApplicationException e) {
            return false;
        }
        //check if exist locally and created if needed
        if (!socialRepository.userExists(request.getUserBlockId())) {
            socialRepository.createResource(List.of(request.userBlockId), SocialRepository.TypeCreate.USER);
        }
        if (!socialRepository.userExists(request.getUserBlockedId())) {
            socialRepository.createResource(List.of(request.userBlockedId), SocialRepository.TypeCreate.USER);
        }

        socialRepository.blockUnblock(request);

        SocialHomeTimelineBlock socialHomeTimelineBlock = SocialConverter.toHomeBlock(request);
        socialHomeTimelineBlockPublisher.publish(socialHomeTimelineBlock);
        return true;
    }

    /**
     * Gets the users blocked by a specific user.
     * @param userId the user for whom to get the blocked users
     * @return a list of userIds who are blocked by the specified userId
     */
    public List<String> getBlockedUsers(String userId) {
        if (!socialRepository.userExists(userId)) {
            return null;
        }

        return socialRepository.getBlockedUsers(userId);
    }

    /**
     * Gets the users who blocked a specific user.
     * @param userId the user for whom to get the users who blocked them
     * @return a list of userIds who have blocked the specified userId
     */
    public List<String> getUsersWhoBlocked(String userId) {
        if (!socialRepository.userExists(userId)) {
            return null;
        }

        return socialRepository.getUsersWhoBlocked(userId);
    }

    /**
     * Creates or updates the like relation between one user and one post.
     * @param request the request indicating who like or unlike which post
     */
    public boolean likeUnlike(AppreciationRequest request) {
        //check if user and post exist in their repo
        try {
            if (userRestClient.getUser(new ObjectId(request.userId)).getStatus() != Response.Status.OK.getStatusCode()
                ||
                postRestClient.getPost(new ObjectId(request.postId)).getStatus() != Response.Status.OK.getStatusCode())
            {
                return false;
            }
        }
        catch (ClientWebApplicationException e) {
            return false;
        }
        //check if exist locally and created if needed
        if (!socialRepository.userExists(request.getUserId())) {
            socialRepository.createResource(List.of(request.getUserId()), SocialRepository.TypeCreate.USER);
        }
        if (!socialRepository.postExists(request.getPostId())) {
            socialRepository.createResource(List.of(request.getPostId()), SocialRepository.TypeCreate.POST);
        }
        // check if the exact same like already exists (same post and same user)
        List<String> likeUsers = socialRepository.getLikesPosts(request.getUserId()).stream().map(post -> post.getPostId().toString()).toList();
        if (request.isLikeUnlike() && likeUsers != null && likeUsers.contains(request.getPostId())) {
            return true;
        }

        socialRepository.likeUnlike(request);

        SocialHomeTimelineLike socialHomeTimelineLike = SocialConverter.toHomeLike(request);
        LikeTimeline likeTimeline = SocialConverter.toUserLike(request);
        socialHomeTimelineLikePublisher.publish(socialHomeTimelineLike);
        likeTimelinePublisher.publish(likeTimeline);
        return true;
    }

    /**
     * Gets the users who liked a specific post.
     * @param postId the post for which to get the users who liked it
     * @return a list of userIds who liked the specified post
     */
    public List<String> getLikeUsers(String postId) {
        if (!socialRepository.postExists(postId)) {
            return null;
        }

        return socialRepository.getLikeUsers(postId);
    }

    /**
     * Gets the posts liked by a specific user.
     * @param userId the user for whom to get the posts they liked
     * @return a list of postIds that the specified userId liked
     */
    public List<LikedPostInfo> getLikesPosts(String userId) {
        if (!socialRepository.userExists(userId)) {
            return null;
        }

        return socialRepository.getLikesPosts(userId);
    }

     /**
     * Checks if two users have mutually blocked each other.
     * @param blockedRelationRequest the request indicating which users to check
     * @return a BlockedRelationResponse that indicates the blocked relation
     */
    public BlockedRelationResponse checkPostBlocked(BlockedRelationRequest blockedRelationRequest) {
        String userId = blockedRelationRequest.getUserId().toString();
        String parentUserId = blockedRelationRequest.getParentId().toString();

        List<String> blockedUsers = socialRepository.getBlockedUsers(userId);
        List<String> blockedByUsers = socialRepository.getUsersWhoBlocked(userId);
        boolean userBlockedParentUser = blockedUsers.contains(parentUserId);
        boolean parentUserBlockedUser = blockedByUsers.contains(parentUserId);

        return new BlockedRelationResponse(parentUserBlockedUser, userBlockedParentUser);
    }
}
