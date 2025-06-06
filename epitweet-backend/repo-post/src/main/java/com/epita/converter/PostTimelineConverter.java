package com.epita.converter;

import com.epita.contracts.post.PostResponse;
import com.epita.payloads.homeTimeline.PostHomeTimeline;
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
                post.getUserId(),
                post.getId().toString(),
                post.getUpdatedAt(),
                method
        );
    }

    /**
     * Converts a {@code postResponse} to a {@code PostHomeTimeline} payload.
     *
     * @param postResponse  The postResponse of the post created or deleted
     * @param method        The method (creation or deletion)
     * @return The converted {@code PostHomeTimeline} payload.
     */

    public static PostHomeTimeline toPostHomeTimeline(PostResponse postResponse, String method) {
        return new PostHomeTimeline(
            postResponse,
            method
        );
    }
}
