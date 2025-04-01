package com.epita.converter;

import com.epita.payloads.userTimeline.PostTimeline;
import com.epita.repository.entity.Post;

public class PostTimelineConverter {
    /**
     * Converts a {@code post} to a {@code PostTimeline} payload.
     *
     * @param post      The post created or deleted
     * @param method    The method (creation or deletion)
     * @return The converted {@code PostTimeline} payload.
     */

    public static PostTimeline toPostTimeline(Post post, String method) {
        return new PostTimeline(
                post.userId,
                post._id.toString(),
                post.updatedAt,
                method
        );
    }
}
