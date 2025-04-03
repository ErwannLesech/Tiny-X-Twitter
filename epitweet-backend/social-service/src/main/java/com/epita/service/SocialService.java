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
    public void followUnfollow(FollowUnfollowRequest request) {
        socialRepository.followUnfollow(request);
    }

    public List<String> getFollows(String userId) {
        if (!socialRepository.userExists(userId)) {
            return null;
        }

        return socialRepository.getFollows(userId);
    }

    public List<String> getFollowers(String userId) {
        if (!socialRepository.userExists(userId)) {
            return null;
        }

        return socialRepository.getFollowers(userId);
    }

    public void blockUnblock(BlockUnblockRequest request) {
        socialRepository.blockUnblock(request);
    }

    public List<String> getBlockedUsers(String userId) {
        if (!socialRepository.userExists(userId)) {
            return null;
        }

        return socialRepository.getBlockedUsers(userId);
    }

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
