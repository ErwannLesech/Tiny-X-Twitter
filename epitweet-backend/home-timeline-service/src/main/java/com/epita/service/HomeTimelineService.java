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
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeTimelineService.class);

    /**
     * Retrieves the timeline for a given {@code ObjectId} userId.
     *
     * @param userId the ID of the user
     * @return The {@code List<HomeTimelinePost>} timeline wrapped in {@code Response}
     */
    public HomeTimelineResponse getHomeTimeline(final ObjectId userId) {
        List<HomeTimelinePost> timeline = homeRepository.getTimeline(userId).stream()
                .map(HomeTimelineConverter::toPost)
                .toList();
        return new HomeTimelineResponse(userId, timeline);
    }

    /**
     * Add/Remove a post from a user timeline when a post is either create or delete.
     * @param message The {@code PostHomeTimeline} message received
     */
    public void updateOnPost(PostHomeTimeline message) {
        List<ObjectId> followers = socialRestClient
                .getFollowers(message.getPost().getUserId().toString())
                .getEntity().stream()
                .map(ObjectId::new)
                .toList();
        for (ObjectId followerId : followers) {
            if (Objects.equals(message.getMethod(), "creation")) {
                HomeTimelineEntry entry = HomeTimelineConverter.PostToEntry(message, followerId);
                homeRepository.addHomeEntry(entry);
            } else {
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
     * @param message The {@code SocialHomeTimelineLike} message received
     */
    public void updateOnLike(SocialHomeTimelineLike message) {
        PostResponse post = postRestClient
                .getPost(message.getPostId())
                .getEntity();
        if (post != null) {
            LOGGER.info("Update user timeline on liked/unliked post");
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
                    homeRepository.addHomeEntry(entry);
                } else {
                    homeRepository.removeHomeEntry(entry, EntryType.LIKE);
                }
            }
        } else {
            LOGGER.error("Unable to add post to timeline");
        }
    }

    /**
     * Add/Remove a post from a user timeline when a user is either follow or unfollow.
     * @param message The {@code SocialHomeTimelineFollow} message received
     */
    public void updateOnFollow(SocialHomeTimelineFollow message) {
        List<PostResponse> posts = postRestClient.getPosts(message.getUserId()).getEntity();
        if (posts != null && !posts.isEmpty()) {
            LOGGER.info("Update user timeline on follow/unfollow user");
            for (PostResponse post : posts) {
                BlockedRelationRequest request = new BlockedRelationRequest(
                        message.getUserId(),
                        post.getUserId());
                BlockedRelationResponse blockedRelationResponse = socialRestClient
                        .getBlockedRelation(request)
                        .getEntity();
                if (Objects.equals(message.getMethod(), "follow")
                && !blockedRelationResponse.getUserBlockedParentUser()) {
                    HomeTimelineEntry entry = HomeTimelineConverter.FollowToEntry(message, post);
                    homeRepository.addHomeEntry(entry);
                } else {
                    ObjectId userId = message.getUserId();
                    ObjectId followedId = message.getUserFollowedId();
                    ObjectId postId = post.get_id();
                    HomeTimelineEntry entry = entryToDelete(userId, followedId, postId);
                    homeRepository.removeHomeEntry(entry, EntryType.POST);
                    homeRepository.removeHomeEntry(entry, EntryType.LIKE);
                }
            }
        } else {
            LOGGER.error("Unable to update user timeline");
        }
    }

    /**
     * Add/Remove a post from a user timeline when a user is either block or unblock.
     * @param message The {@code SocialHomeTimelineBlock} message received
     */
    public void updateOnBlock(SocialHomeTimelineBlock message) {
        List<PostResponse> posts = postRestClient.getPosts(message.getUserId()).getEntity();
        if (posts != null && !posts.isEmpty()) {
            LOGGER.info("Update user timeline on blocked/unblocked user");
            for (PostResponse post : posts) {
                if (Objects.equals(message.getMethod(), "block")) {
                    ObjectId userId = message.getUserId();
                    ObjectId blockedId = message.getUserBlockedId();
                    ObjectId postId = post.get_id();
                    HomeTimelineEntry entry = entryToDelete(userId, blockedId, postId);
                    homeRepository.removeHomeEntry(entry, EntryType.POST);
                    homeRepository.removeHomeEntry(entry, EntryType.LIKE);
                }
            }
        }
    }

    /**
     * Utility private method to create a {@code HomeTimelineEntry}
     * @param userId {@code ObjectId}
     * @param userFollowedId {@code ObjectId}
     * @param postId {@code ObjectId}
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
