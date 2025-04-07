package com.epita.converter;

import com.epita.payloads.homeTimeline.PostHomeTimeline;
import com.epita.payloads.social.BlockUser;
import com.epita.payloads.social.FollowUser;
import com.epita.payloads.social.LikePost;
import com.epita.repository.entity.EntryType;
import com.epita.repository.entity.HomeTimelineEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static HomeTimelineEntry LikeToEntry(LikePost likePost) {
        HomeTimelineEntry entry = new HomeTimelineEntry();
        entry.setUserFollowedId(likePost.userId());
        entry.setDate(likePost.postLikeDate());
        entry.setType(EntryType.LIKE);
        return entry;
    }

    /**
     * Converts a {@code PostRequest} to a {@code Post} entity.
     *
     * @param followUser The {@code FollowUser} to convert.
     * @return The converted {@code HomeTimelineEntry} entity.
     */
    public static HomeTimelineEntry FollowToEntry(FollowUser followUser) {
        HomeTimelineEntry entry = new HomeTimelineEntry();
        entry.setUserId(followUser.userId());
        entry.setUserFollowedId(followUser.userFollowedId());
        return entry;
    }

    /**
     * Converts a {@code PostRequest} to a {@code Post} entity.
     *
     * @param blockUser The {@code BlockUser} to convert.
     * @return The converted {@code HomeTimelineEntry} entity.
     */
    public static HomeTimelineEntry BlockToEntry(BlockUser blockUser) {
        HomeTimelineEntry entry = new HomeTimelineEntry();
        entry.setUserId(blockUser.userId());
        entry.setUserFollowedId(blockUser.userBlockedId());
        return entry;
    }

    /**
     * Converts a {@code PostRequest} to a {@code Post} entity.
     *
     * @param post The {@code PostHomeTimeline} to convert.
     * @return The converted {@code HomeTimelineEntry} entity.
     */
    public static HomeTimelineEntry PostToEntry(PostHomeTimeline post) {
        HomeTimelineEntry entry = new HomeTimelineEntry();
        entry.setUserFollowedId(post.getPost().getUserId());
        entry.setDate(post.getPost().createdAt);
        entry.setPost(post.getPost());
        entry.setType(EntryType.POST);
        return entry;
    }
}
