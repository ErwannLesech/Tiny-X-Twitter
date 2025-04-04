package com.epita.service;

import com.epita.contracts.post.PostResponse;
import com.epita.controller.contracts.AppreciationRequest;
import com.epita.controller.contracts.BlockUnblockRequest;
import com.epita.controller.contracts.FollowUnfollowRequest;
import com.epita.converter.SocialConverter;
import com.epita.payloads.homeTimeline.SocialHomeTimelineBlock;
import com.epita.payloads.homeTimeline.SocialHomeTimelineFollow;
import com.epita.payloads.homeTimeline.SocialHomeTimelineLike;
import com.epita.payloads.post.CreatePostRequest;
import com.epita.payloads.post.CreatePostResponse;
import com.epita.payloads.userTimeline.LikeTimeline;
import com.epita.repository.PostRestClient;
import com.epita.repository.SocialRepository;
import com.epita.repository.UserRestClient;
import com.epita.repository.publisher.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class SocialService {
    @Inject
    SocialRepository socialRepository;

    @Inject
    UserRestClient userRestClient;

    @Inject
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
    IsPostBlockedPublisher isPostBlockedPublisher;

    /**
     * Creates or updates the follow relation between two users.
     * @param request the request indicating who follows or unfollows whom
     */
    public boolean followUnfollow(FollowUnfollowRequest request) {
        socialRepository.followUnfollow(request);

        if (userRestClient.getUser(request.userFollowId) == null || userRestClient.getUser(request.userFollowedId) == null) {
            return false;
        }

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
        socialRepository.blockUnblock(request);

        if (userRestClient.getUser(request.userBlockId) == null || userRestClient.getUser(request.userBlockedId) == null) {
            return false;
        }

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

    public boolean likeUnlike(AppreciationRequest request) {
        socialRepository.likeUnlike(request);

        if (userRestClient.getUser(request.userId) == null || postRestClient.getPost(request.postId) == null) {
            return false;
        }

        SocialHomeTimelineLike socialHomeTimelineLike = SocialConverter.toHomeLike(request);
        LikeTimeline likeTimeline = SocialConverter.toUserLike(request);

        socialHomeTimelineLikePublisher.publish(socialHomeTimelineLike);
        likeTimelinePublisher.publish(likeTimeline);
        return true;
    }

    public List<String> getLikeUsers(String postId) {
        if (!socialRepository.postExists(postId)) {
            return null;
        }

        return socialRepository.getLikeUsers(postId);
    }

    public List<String> getLikesPosts(String userId) {
        if (!socialRepository.userExists(userId)) {
            return null;
        }

        return socialRepository.getLikesPosts(userId);
    }

    //méthode pour redis post, appelée depuis le subscriber
    public void checkPostBlocked(CreatePostRequest message) {
        String userId = message.getUserId().toString();

        PostResponse post = postRestClient.getPost(message.getParentId().toString());
        String parentUserId = post.userId.toString();

        List<String> blockedUsers = socialRepository.getBlockedUsers(userId);
        List<String> blockedByUsers = socialRepository.getUsersWhoBlocked(userId);
        boolean userBlockedParentUser = blockedUsers.contains(parentUserId);
        boolean parentUserBlockedUser = blockedByUsers.contains(parentUserId);


        //publisher
        CreatePostResponse postResponse = new CreatePostResponse(
                message.getUserId(),
                message.getPostType(),
                message.getContent(),
                message.getMediaUrl(),
                message.getParentId(),
                parentUserBlockedUser,
                userBlockedParentUser
        );

        isPostBlockedPublisher.publish(postResponse);

    }
}
