package com.epita.service;

import com.epita.contracts.post.PostResponse;
import com.epita.converter.PayloadConverter;
import com.epita.payloads.homeTimeline.PostHomeTimeline;
import com.epita.payloads.social.BlockUser;
import com.epita.payloads.social.FollowUser;
import com.epita.payloads.social.LikePost;
import com.epita.repository.HomeTimelineRepository;
import com.epita.repository.RepoPostRestClient;
import com.epita.repository.entity.EntryType;
import com.epita.repository.entity.HomeTimelineEntry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.jboss.logging.annotations.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class HomeTimelineService {

    @Inject
    HomeTimelineRepository homeRepository;

    @Inject
    RepoPostRestClient repoPostRestClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeTimelineService.class);


    public Response getTimeline(final ObjectId userId) {
        if (userId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<PostResponse> timeline = homeRepository.getTimeline(userId).stream()
                .map(HomeTimelineEntry::getPost)
                .toList();

        return ( !timeline.isEmpty() ) ? Response.ok(timeline).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    public void updateOnPost(PostHomeTimeline message) {
        List<ObjectId> followers = homeRepository.getFollowers(message.getPost().userId);
        for (ObjectId followerId : followers ) {
            if (Objects.equals(message.getMethod(), "creation")) {
                HomeTimelineEntry entry = PayloadConverter.PostToEntry(message);
                entry.setUserId(followerId);
                homeRepository.addHomeEntry(entry);
            } else {
                homeRepository.removeHomeEntry(followerId, message.getPost().getUserId(), message.getPost().get_id(), null);
            }
        }
    }

    public void updateOnLike(LikePost message) {
        PostResponse post = repoPostRestClient.getPost(message.postId().toString());
        if (post != null) {
            LOGGER.info("Update user timeline on liked/unliked post");
            List<ObjectId> followers = homeRepository.getFollowers(message.userId());
            for (ObjectId followerId : followers ) {
                post.setCreatedAt(message.postLikeDate());
                HomeTimelineEntry entry = PayloadConverter.LikeToEntry(message);
                entry.setUserId(followerId);
                entry.setPost(post);
                if (Objects.equals(message.method(), "like") && !homeRepository.isBlocked(followerId, post.getUserId())) {
                    homeRepository.addHomeEntry(entry);
                } else {
                    homeRepository.removeHomeEntry(followerId, message.userId(), post.get_id(), EntryType.LIKE);
                }
            }
        } else {
            LOGGER.error("Unable to add post to timeline");
        }
    }

    public void updateOnBlock(BlockUser message) {
        List<PostResponse> posts = repoPostRestClient.getPosts(message.userId().toString());
        if (posts != null && !posts.isEmpty()) {
            LOGGER.info("Update user timeline on blocked/unblocked user");
            for (PostResponse post : posts) {
                if (Objects.equals(message.method(), "block")) {
                    homeRepository.addBlockedUser(message);
                    homeRepository.removeHomeEntry(message.userId(), message.userBlockedId(), post.get_id(), null);
                } else {
                    homeRepository.removeBlockedUser(message);
                }
            }
        }
    }

    public void updateOnFollow(FollowUser message) {
        List<PostResponse> posts = repoPostRestClient.getPosts(message.userId().toString());
        if (posts != null && !posts.isEmpty()) {
            LOGGER.info("Update user timeline on follow/unfollow user");
            for (PostResponse post : posts) {
                if (Objects.equals(message.method(), "follow") && !homeRepository.isBlocked(message.userId(), post.getUserId())) {
                    HomeTimelineEntry entry = PayloadConverter.FollowToEntry(message);
                    entry.setDate(post.createdAt);
                    entry.setType(EntryType.POST);
                    entry.setPost(post);
                    homeRepository.addHomeEntry(entry);
                } else {
                    homeRepository.removeHomeEntry(message.userId(), message.userFollowedId(), post.get_id(), null);
                }
            }
        } else {
            LOGGER.error("Unable to update user timeline");
        }
    }
}
