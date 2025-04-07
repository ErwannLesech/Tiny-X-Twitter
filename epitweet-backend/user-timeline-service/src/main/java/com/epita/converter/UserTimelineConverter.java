package com.epita.converter;

import com.epita.controller.contracts.UserTimelinePost;
import com.epita.payloads.userTimeline.LikeTimeline;
import com.epita.payloads.userTimeline.PostTimeline;
import com.epita.repository.entity.UserTimelineEntry;
import com.epita.repository.entity.UserTimelineEntryAction;

import java.time.ZoneId;

/**
 * Utility class for converting between different representations of user timeline data.
 */
public class UserTimelineConverter {
    /**
     * Converts a {@link PostTimeline} payload into a {@link UserTimelineEntry} entity.
     * <p>
     * The resulting entry represents a post created by the user.
     *
     * @param postTimeline The {@link PostTimeline} payload representing a post creation event.
     * @return A {@link UserTimelineEntry} entity corresponding to the creation action.
     */
    public static UserTimelineEntry toEntity(PostTimeline postTimeline) {
        return new UserTimelineEntry(
                postTimeline.getUserId(),
                postTimeline.getPostId(),
                UserTimelineEntryAction.CREATE,
                postTimeline.getPostModificationDate()
        );
    }

    /**
     * Converts a {@link LikeTimeline} payload into a {@link UserTimelineEntry} entity.
     * <p>
     * The resulting entry represents a post liked by the user.
     * The like timestamp is converted to an {@link java.time.Instant} using the system's default timezone.
     *
     * @param likeTimeline The {@link LikeTimeline} payload representing a like action.
     * @return A {@link UserTimelineEntry} entity corresponding to the like action.
     */
    public static UserTimelineEntry toEntity(LikeTimeline likeTimeline) {
        return new UserTimelineEntry(
                likeTimeline.getUserId(),
                likeTimeline.getPostId(),
                UserTimelineEntryAction.LIKE,
                likeTimeline.getPostLikeDate().atZone(ZoneId.systemDefault()).toInstant()
        );
    }

    /**
     * Converts a {@link UserTimelineEntry} entity into a {@link UserTimelinePost}.
     *
     * @param userTimelineEntry The {@link UserTimelineEntry} entity to convert.
     * @return A {@link UserTimelinePost} representing the entry in the API response format.
     */
    public static UserTimelinePost toResponse(UserTimelineEntry userTimelineEntry) {
        return new UserTimelinePost(
                userTimelineEntry.getPostId(),
                userTimelineEntry.getUserTimelineEntryAction().toString(),
                userTimelineEntry.getPerformedAt()
        );
    }
}
