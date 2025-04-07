package com.epita;

import com.epita.controller.contracts.UserTimelinePost;
import com.epita.converter.UserTimelineConverter;
import com.epita.payloads.userTimeline.LikeTimeline;
import com.epita.payloads.userTimeline.PostTimeline;
import com.epita.repository.entity.UserTimelineEntry;
import com.epita.repository.entity.UserTimelineEntryAction;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

public class UserTimelineConverterTest {

    @Test
    public void testToEntity_fromPostTimeline_shouldConvertCorrectly() {
        ObjectId userId = new ObjectId();
        String postId = "post123";
        Instant now = Instant.now();

        PostTimeline postTimeline = new PostTimeline(userId, postId, now, "creation");

        UserTimelineEntry entity = UserTimelineConverter.toEntity(postTimeline);

        assertEquals(userId, entity.getUserId());
        assertEquals(postId, entity.getPostId());
        assertEquals(UserTimelineEntryAction.CREATE, entity.getUserTimelineEntryAction());
        assertEquals(now, entity.getPerformedAt());
    }

    @Test
    public void testToEntity_fromLikeTimeline_shouldConvertCorrectly() {
        ObjectId userId = new ObjectId();
        String postId = "post456";
        LocalDateTime localDateTime = LocalDateTime.now();
        Instant expectedInstant = localDateTime.atZone(TimeZone.getDefault().toZoneId()).toInstant();

        LikeTimeline likeTimeline = new LikeTimeline(userId, postId, localDateTime, "like");

        UserTimelineEntry entity = UserTimelineConverter.toEntity(likeTimeline);

        assertEquals(userId, entity.getUserId());
        assertEquals(postId, entity.getPostId());
        assertEquals(UserTimelineEntryAction.LIKE, entity.getUserTimelineEntryAction());
        assertEquals(expectedInstant, entity.getPerformedAt());
    }

    @Test
    public void testToResponse_shouldConvertCorrectly() {
        ObjectId userId = new ObjectId();
        String postId = "post789";
        Instant performedAt = Instant.now();
        UserTimelineEntry entry = new UserTimelineEntry(userId, postId, UserTimelineEntryAction.LIKE, performedAt);

        UserTimelinePost post = UserTimelineConverter.toResponse(entry);

        assertEquals(postId, post.getPostId());
        assertEquals(UserTimelineEntryAction.LIKE.toString(), post.getAction());
        assertEquals(performedAt, post.getAt());
    }
}
