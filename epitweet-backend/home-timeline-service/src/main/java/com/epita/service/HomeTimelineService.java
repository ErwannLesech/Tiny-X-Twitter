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
import com.epita.repository.restClients.PostRestClient;
import com.epita.repository.entity.EntryType;
import com.epita.repository.entity.HomeTimelineEntry;
import com.epita.repository.restClients.SocialRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

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

    public Response getHomeTimeline(final ObjectId userId) {
        if (userId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        List<HomeTimelinePost> timeline = homeRepository.getTimeline(userId).stream()
                .map(HomeTimelineConverter::toPost)
                .toList();
        HomeTimelineResponse response = new HomeTimelineResponse(userId, timeline);
        return Response.ok(response).build();
    }

    public void updateOnPost(PostHomeTimeline message) {
        List<ObjectId> followers = socialRestClient.getFollowers(message.getPost().getUserId().toString()).getEntity().stream()
                .map(ObjectId::new)
                .toList();
        for (ObjectId followerId : followers) {
            if (Objects.equals(message.getMethod(), "creation")) {
                HomeTimelineEntry entry = HomeTimelineConverter.PostToEntry(message, followerId);
                homeRepository.addHomeEntry(entry);
            } else {
                homeRepository.removeHomeEntry(followerId, message.getPost().getUserId(), message.getPost().get_id(), null);
            }
        }
    }

    public void updateOnLike(SocialHomeTimelineLike message) {
        PostResponse post = postRestClient.getPost(message.getPostId()).getEntity();
        if (post != null) {
            LOGGER.info("Update user timeline on liked/unliked post");
            List<ObjectId> followers = socialRestClient.getFollowers(message.getUserId().toString()).getEntity().stream()
                    .map(ObjectId::new)
                    .toList();
            for (ObjectId followerId : followers) {
                post.setCreatedAt(message.getPostLikeDate().atZone(ZoneId.systemDefault()).toInstant());
                HomeTimelineEntry entry = HomeTimelineConverter.LikeToEntry(message, followerId, post);

                BlockedRelationResponse blockedRelationResponse = socialRestClient.getBlockedRelation(new BlockedRelationRequest(followerId, post.getUserId())).getEntity();
                if (Objects.equals(message.getMethod(), "like") && !blockedRelationResponse.getUserBlockedParentUser()) {
                    homeRepository.addHomeEntry(entry);
                } else {
                    homeRepository.removeHomeEntry(followerId, message.getUserId(), post.get_id(), EntryType.LIKE);
                }
            }
        } else {
            LOGGER.error("Unable to add post to timeline");
        }
    }

    public void updateOnFollow(SocialHomeTimelineFollow message) {
        List<PostResponse> posts = postRestClient.getPosts(message.getUserId()).getEntity();
        if (posts != null && !posts.isEmpty()) {
            LOGGER.info("Update user timeline on follow/unfollow user");
            for (PostResponse post : posts) {
                BlockedRelationResponse blockedRelationResponse = socialRestClient.getBlockedRelation(new BlockedRelationRequest(message.getUserId(), post.getUserId())).getEntity();
                if (Objects.equals(message.getMethod(), "follow") && !blockedRelationResponse.getUserBlockedParentUser()) {
                    HomeTimelineEntry entry = HomeTimelineConverter.FollowToEntry(message, post);
                    homeRepository.addHomeEntry(entry);
                } else {
                    homeRepository.removeHomeEntry(message.getUserId(), message.getUserFollowedId(), post.get_id(), null);
                }
            }
        } else {
            LOGGER.error("Unable to update user timeline");
        }
    }

    public void updateOnBlock(SocialHomeTimelineBlock message) {
        List<PostResponse> posts = postRestClient.getPosts(message.getUserId()).getEntity();
        if (posts != null && !posts.isEmpty()) {
            LOGGER.info("Update user timeline on blocked/unblocked user");
            for (PostResponse post : posts) {
                if (Objects.equals(message.getMethod(), "block")) {
                    homeRepository.removeHomeEntry(message.getUserId(), message.getUserBlockedId(), post.get_id(), null);
                }
            }
        }
    }
}
