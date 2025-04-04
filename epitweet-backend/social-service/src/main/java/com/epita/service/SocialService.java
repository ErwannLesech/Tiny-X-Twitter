package com.epita.service;

import com.epita.controller.contracts.AppreciationRequest;
import com.epita.controller.contracts.BlockUnblockRequest;
import com.epita.controller.contracts.FollowUnfollowRequest;
import com.epita.repository.SocialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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

    public void likeUnlike(AppreciationRequest request) {
        socialRepository.likeUnlike(request);
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
}
