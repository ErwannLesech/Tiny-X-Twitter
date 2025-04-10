package com.epita.converter;

import com.epita.contracts.post.PostResponse;
import com.epita.controller.contracts.HomeTimelinePost;
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
public class HomeTimelineConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeTimelineConverter.class);

    public static HomeTimelinePost toPost(final HomeTimelineEntry entry) {
        return new HomeTimelinePost(entry.getUserFollowedId(), entry.getPostId(), entry.getPostType(), entry.getDate());
    }
    /**
     * Converts a {@code SocialHomeTimelineLike} to a {@code HomeTimelineEntry} entity.
     *
     * @param likePost The {@code SocialHomeTimelineLike} to convert.
     * @param followerId The {@code ObjectId} field to set.
     * @param post The {@code PostResponse} field to set.
     * @return The converted {@code HomeTimelineEntry} entity.
     */
    public static HomeTimelineEntry LikeToEntry(SocialHomeTimelineLike likePost, ObjectId followerId, PostResponse post) {
        HomeTimelineEntry entry = new HomeTimelineEntry();
        entry.setUserFollowedId(likePost.getUserId());
        entry.setDate(likePost.getPostLikeDate().atZone(ZoneId.systemDefault()).toInstant());
        entry.setPostType(EntryType.LIKE);
        entry.setUserId(followerId);
        entry.setPostId(post.get_id());
        return entry;
    }

    /**
     * Converts a {@code SocialHomeTimelineFollow} to a {@code HomeTimelineEntry} entity.
     *
     * @param followUser The {@code FollowUser} to convert.
     * @param post The {@code PostResponse} field to set.
     * @return The converted {@code HomeTimelineEntry} entity.
     */
    public static HomeTimelineEntry FollowToEntry(SocialHomeTimelineFollow followUser, PostResponse post) {
        HomeTimelineEntry entry = new HomeTimelineEntry();
        entry.setUserId(followUser.getUserId());
        entry.setUserFollowedId(followUser.getUserFollowedId());
        entry.setDate(post.createdAt);
        entry.setPostType(EntryType.POST);
        entry.setPostId(post.get_id());
        return entry;
    }

    /**
     * Converts a {@code PostHomeTimeline} to a {@code HomeTimelineEntry} entity.
     *
     * @param post The {@code PostHomeTimeline} to convert.
     * @param followerId The {@code ObjectId} field to set.
     * @return The converted {@code HomeTimelineEntry} entity.
     */
    public static HomeTimelineEntry PostToEntry(PostHomeTimeline post, ObjectId followerId) {
        HomeTimelineEntry entry = new HomeTimelineEntry();
        entry.setUserFollowedId(post.getPost().getUserId());
        entry.setDate(post.getPost().createdAt);
        entry.setPostId(post.getPost().get_id());
        entry.setPostType(EntryType.POST);
        entry.setUserId(followerId);
        return entry;
    }
}
