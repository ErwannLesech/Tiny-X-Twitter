package com.epita.service;

import com.epita.controller.contracts.AppreciationRequest;
import com.epita.controller.contracts.BlockUnblockRequest;
import com.epita.controller.contracts.FollowUnfollowRequest;
import com.epita.repository.SocialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class SocialService {
    @Inject
    SocialRepository socialRepository;

    /**
     * Creates or updates the follow relation between two users.
     * @param request the request indicating who follows or unfollows whom
     */
    public void followUnfollow(FollowUnfollowRequest request) {
        if (!socialRepository.userExists(request.getUserFollowId())) {
            throw new NotFoundException("User " + request.getUserFollowId() + " not found");
        }
        if (!socialRepository.userExists(request.getUserFollowedId())) {
            throw new NotFoundException("User " + request.getUserFollowedId() + " not found");
        }

        socialRepository.followUnfollow(request);
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
    public void blockUnblock(BlockUnblockRequest request) {
        if (!socialRepository.userExists(request.getUserBlockId())) {
            throw new NotFoundException("User " + request.getUserBlockId()+ " not found");
        }
        if (!socialRepository.userExists(request.getUserBlockedId())) {
            throw new NotFoundException("User " + request.getUserBlockedId() + " not found");
        }

        socialRepository.blockUnblock(request);
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
    public void likeUnlike(AppreciationRequest request) {
        if (!socialRepository.userExists(request.getUserId())) {
            throw new NotFoundException("User " + request.getUserId() + " not found");
        }
        if (!socialRepository.postExists(request.getPostId())) {
            throw new NotFoundException("User " + request.getPostId() + " not found");
        }

        socialRepository.likeUnlike(request);
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
    public List<String> getLikesPosts(String userId) {
        if (!socialRepository.userExists(userId)) {
            return null;
        }

        return socialRepository.getLikesPosts(userId);
    }
}
