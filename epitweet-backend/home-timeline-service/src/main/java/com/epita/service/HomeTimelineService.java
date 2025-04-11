package com.epita.service;

import com.epita.contracts.post.PostResponse;
import com.epita.contracts.social.BlockedRelationRequest;
import com.epita.contracts.social.BlockedRelationResponse;
import com.epita.controller.contracts.HomeTimelinePost;
import com.epita.controller.contracts.HomeTimelineResponse;
import com.epita.converter.HomeTimelineConverter;
import com.epita.payloads.homeTimeline.PostHomeTimeline;
import com.epita.payloads.homeTimeline.SocialHomeTimelineBlock;
import com.epita.payloads.homeTimeline.SocialHomeTimelineFollow;
import com.epita.payloads.homeTimeline.SocialHomeTimelineLike;
import com.epita.repository.HomeTimelineRepository;
import com.epita.repository.entity.EntryType;
import com.epita.repository.restClient.PostRestClient;
import com.epita.repository.entity.HomeTimelineEntry;
import com.epita.repository.restClient.SocialRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.antlr.v4.runtime.misc.ObjectEqualityComparator;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

/**
 * Service for managing HomeTimeline entry.
 */
@ApplicationScoped
public class HomeTimelineService {

    @Inject
    HomeTimelineRepository homeRepository;

    @Inject
    @RestClient
    PostRestClient postRestClient;

    @Inject
    @RestClient
    SocialRestClient socialRestClient;

    @Inject
    Logger logger;

    /**
     * Retrieves the timeline for a given {@code ObjectId} userId.
     *
     * @param userId the ID of the user
     * @return The {@code List<HomeTimelinePost>} timeline wrapped in {@code Response}
     */
    public HomeTimelineResponse getHomeTimeline(final ObjectId userId) {
        logger.infof("Getting home timeline for %s", userId);
        List<HomeTimelinePost> timeline = homeRepository.getTimeline(userId).stream()
                .map(HomeTimelineConverter::toPost)
                .toList();
        return new HomeTimelineResponse(userId, timeline);
    }

    /**
     * Add/Remove a post from a user timeline when a post is either create or delete.
     *
     * @param message The {@code PostHomeTimeline} message received
     */
    public void updateOnPost(PostHomeTimeline message) {

        List<ObjectId> followers = socialRestClient
                .getFollowers(message.getPost().getUserId().toString())
                .getEntity().stream()
                .map(ObjectId::new)
                .toList();
        logger.info("Update user timeline on creation/deletion post");
        for (ObjectId followerId : followers) {
            if (Objects.equals(message.getMethod(), "creation")) {
                logger.infof("Add post %s to the home timeline of %s", message.getPost().get_id(), followerId);
                HomeTimelineEntry entry = HomeTimelineConverter.PostToEntry(message, followerId);
                homeRepository.addHomeEntry(entry);
            } else {
                logger.infof("Remove post %s from the home timeline of %s",
                        message.getPost().get_id(), followerId);
                ObjectId postUserId = message.getPost().getUserId();
                ObjectId postId = message.getPost().get_id();
                HomeTimelineEntry entry = entryToDelete(followerId, postUserId, postId);
                homeRepository.removeHomeEntry(entry, EntryType.POST);
                homeRepository.removeHomeEntry(entry, EntryType.LIKE);
            }
        }
    }

    /**
     * Add/Remove a post from a user timeline when a post is either like or unlike.
     *
     * @param message The {@code SocialHomeTimelineLike} message received
     */
    public void updateOnLike(SocialHomeTimelineLike message) {
        PostResponse post = postRestClient
                .getPost(message.getPostId())
                .getEntity();
        if (post != null) {
            logger.info("Update user timeline on liked/unliked post");
            List<ObjectId> followers = socialRestClient.getFollowers(message.getUserId().toString())
                    .getEntity().stream()
                    .map(ObjectId::new)
                    .toList();
            for (ObjectId followerId : followers) {
                post.setCreatedAt(message.getPostLikeDate().atZone(ZoneId.systemDefault()).toInstant());
                HomeTimelineEntry entry = HomeTimelineConverter.LikeToEntry(message, followerId, post);

                BlockedRelationRequest request = new BlockedRelationRequest(followerId, post.getUserId());
                BlockedRelationResponse blockedRelationResponse = socialRestClient
                        .getBlockedRelation(request)
                        .getEntity();
                if (Objects.equals(message.getMethod(), "like")
                        && !blockedRelationResponse.getUserBlockedParentUser()) {
                    logger.infof("Add post %s to the home timeline of %s", post.get_id(), followerId);
                    homeRepository.addHomeEntry(entry);
                } else {
                    logger.infof("Remove post %s from the home timeline of %s", post.get_id(), followerId);
                    homeRepository.removeHomeEntry(entry, EntryType.LIKE);
                }
            }
        } else {
            logger.error("Unable to add post to timeline");
        }
    }

    /**
     * Add/Remove a post from a user timeline when a user is either follow or unfollow.
     *
     * @param message The {@code SocialHomeTimelineFollow} message received
     */
    public void updateOnFollow(SocialHomeTimelineFollow message) {
        logger.infof("User du demandeur de follow: %s", message.getUserId());
        logger.infof("User du receveur de follow: %s", message.getUserFollowedId());

        List<PostResponse> posts = postRestClient.getPosts(message.getUserFollowedId()).getEntity();
        List<ObjectId> likedPosts = socialRestClient.getLikedPosts(message.getUserFollowedId().toString()).getEntity()
                .stream().map(ObjectId::new).toList();
        if (posts != null && !posts.isEmpty()) {
            logger.info("Update user timeline on follow/unfollow user");
            for (PostResponse post : posts) {
                BlockedRelationRequest request = new BlockedRelationRequest(
                        message.getUserId(),
                        post.getUserId());
                BlockedRelationResponse blockedRelationResponse = socialRestClient
                        .getBlockedRelation(request)
                        .getEntity();
                if (Objects.equals(message.getMethod(), "follow")
                        && !blockedRelationResponse.getUserBlockedParentUser()) {
                    logger.infof("Add post %s to the home timeline of %s",
                            post.get_id(), message.getUserFollowedId());
                    HomeTimelineEntry entry = HomeTimelineConverter.FollowToEntry(message, post);
                    homeRepository.addHomeEntry(entry);

                } else {
                    ObjectId userId = message.getUserId();
                    ObjectId followedId = message.getUserFollowedId();
                    logger.infof("Remove every post relate to user %s from the home timeline of %s",
                            followedId, userId);
                    homeRepository.removeUserFromTimeline(userId, followedId);
                }
            }
        } else {
            logger.error("Unable to update user timeline");
        }
    }

    /**
     * Add/Remove a post from a user timeline when a user is either block or unblock.
     *
     * @param message The {@code SocialHomeTimelineBlock} message received
     */
    public void updateOnBlock(SocialHomeTimelineBlock message) {
        if (Objects.equals(message.getMethod(), "block")) {
            ObjectId userId = message.getUserId();
            ObjectId userBlockedId = message.getUserBlockedId();
            logger.infof("Remove every post relate to user %s from the home timeline of %s", userBlockedId, userId);
            homeRepository.removeUserFromTimeline(userId, userBlockedId);
        }
    }

    /**
     * Utility private method to create a {@code HomeTimelineEntry}
     *
     * @param userId         {@code ObjectId}
     * @param userFollowedId {@code ObjectId}
     * @param postId         {@code ObjectId}
     * @return The {@code HomeTimelineEntry} created.
     */
    private HomeTimelineEntry entryToDelete(ObjectId userId, ObjectId userFollowedId, ObjectId postId) {
        HomeTimelineEntry entry = new HomeTimelineEntry();
        entry.setUserId(userId);
        entry.setUserFollowedId(userFollowedId);
        entry.setPostId(postId);
        return entry;
    }
}
