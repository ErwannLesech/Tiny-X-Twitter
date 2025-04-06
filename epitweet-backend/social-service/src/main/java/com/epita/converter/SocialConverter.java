package com.epita.converter;

import com.epita.controller.contracts.AppreciationRequest;
import com.epita.controller.contracts.BlockUnblockRequest;
import com.epita.controller.contracts.FollowUnfollowRequest;
import com.epita.payloads.homeTimeline.SocialHomeTimelineBlock;
import com.epita.payloads.homeTimeline.SocialHomeTimelineFollow;
import com.epita.payloads.homeTimeline.SocialHomeTimelineLike;
import com.epita.payloads.userTimeline.LikeTimeline;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class SocialConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocialConverter.class);

    /**
     * Converts a {@code FollowUnfollowRequest} to a {@code SocialHomeTimelineFollow} entity.
     *
     * @param request      The follow request.
     * @return The converted {@code SocialHomeTimelineFollow} entity.
     */
    public static SocialHomeTimelineFollow toHomeFollow(FollowUnfollowRequest request) {
        return new SocialHomeTimelineFollow(
                new ObjectId(request.userFollowId),
                new ObjectId(request.userFollowedId),
                request.followUnfollow ? "follow" : "unfollow"
        );
    }

    /**
     * Converts a {@code BlockUnblockRequest} to a {@code SocialHomeTimelineBlock} entity.
     *
     * @param request      The block request.
     * @return The converted {@code SocialHomeTimelineBlock} entity.
     */
    public static SocialHomeTimelineBlock toHomeBlock(BlockUnblockRequest request) {
        return new SocialHomeTimelineBlock(
                new ObjectId(request.userBlockId),
                new ObjectId(request.userBlockedId),
                request.blockUnblock ? "block" : "unblock"
        );
    }

    /**
     * Converts a {@code AppreciationRequest} to a {@code SocialHomeTimelineLike} entity.
     *
     * @param request      The appreciation request.
     * @return The converted {@code SocialHomeTimelineLike} entity.
     */
    public static SocialHomeTimelineLike toHomeLike(AppreciationRequest request) {
        return new SocialHomeTimelineLike(
                new ObjectId(request.userId),
                new ObjectId(request.postId),
                LocalDateTime.now(),
                request.likeUnlike ? "like" : "unlike"
        );
    }

    /**
     * Converts a {@code AppreciationRequest} to a {@code LikeTimeline} entity.
     *
     * @param request      The appreciation request.
     * @return The converted {@code SocialHomeTimelineLike} entity.
     */
    public static LikeTimeline toUserLike(AppreciationRequest request) {
        return new LikeTimeline(
                new ObjectId(request.userId),
                request.postId,
                LocalDateTime.now(),
                request.likeUnlike ? "like" : "unlike"
        );
    }
}
