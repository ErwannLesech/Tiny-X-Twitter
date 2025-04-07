package com.epita.converter;

import com.epita.contracts.post.PostResponse;
import com.epita.payloads.homeTimeline.PostHomeTimeline;
import com.epita.payloads.homeTimeline.SocialHomeTimelineFollow;
import com.epita.payloads.homeTimeline.SocialHomeTimelineLike;
import com.epita.repository.entity.EntryType;
import com.epita.repository.entity.HomeTimelineEntry;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;

/**
 * Utility class for converting between different representations of a HomeTimelineEntry.
 */
public class PayloadConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayloadConverter.class);

    /**
     * Converts a {@code PostRequest} to a {@code Post} entity.
     *
     * @param likePost The {@code LikePost} to convert.
     * @return The converted {@code HomeTimelineEntry} entity.
     */
    public static HomeTimelineEntry LikeToEntry(SocialHomeTimelineLike likePost, ObjectId followerId, PostResponse post) {
        HomeTimelineEntry entry = new HomeTimelineEntry();
        entry.setUserFollowedId(likePost.getUserId());
        entry.setDate(likePost.getPostLikeDate().atZone(ZoneId.systemDefault()).toInstant());
        entry.setType(EntryType.LIKE);
        entry.setUserId(followerId);
        entry.setPost(post);
        return entry;
    }

    /**
     * Converts a {@code PostRequest} to a {@code Post} entity.
     *
     * @param followUser The {@code FollowUser} to convert.
     * @return The converted {@code HomeTimelineEntry} entity.
     */
    public static HomeTimelineEntry FollowToEntry(SocialHomeTimelineFollow followUser, PostResponse post) {
        HomeTimelineEntry entry = new HomeTimelineEntry();
        entry.setUserId(followUser.getUserId());
        entry.setUserFollowedId(followUser.getUserFollowedId());
        entry.setDate(post.createdAt);
        entry.setType(EntryType.POST);
        entry.setPost(post);
        return entry;
    }

    /**
     * Converts a {@code PostRequest} to a {@code Post} entity.
     *
     * @param post The {@code PostHomeTimeline} to convert.
     * @return The converted {@code HomeTimelineEntry} entity.
     */
    public static HomeTimelineEntry PostToEntry(PostHomeTimeline post, ObjectId followerId) {
        HomeTimelineEntry entry = new HomeTimelineEntry();
        entry.setUserFollowedId(post.getPost().getUserId());
        entry.setDate(post.getPost().createdAt);
        entry.setPost(post.getPost());
        entry.setType(EntryType.POST);
        entry.setUserId(followerId);
        return entry;
    }
}
