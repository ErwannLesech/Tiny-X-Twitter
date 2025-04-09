package com.epita;

import com.epita.controller.contracts.UserTimelineResponse;
import com.epita.payloads.userTimeline.LikeTimeline;
import com.epita.payloads.userTimeline.PostTimeline;
import com.epita.repository.UserTimelineRepository;
import com.epita.repository.entity.UserTimelineEntry;
import com.epita.service.UserTimelineService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class UserTimelineServiceTest {

    @Inject
    UserTimelineService userTimelineService;

    @InjectMock
    UserTimelineRepository userTimelineRepository;

    @Test
    public void testGetUserTimeline_shouldReturnUserTimelineResponse() {
        ObjectId userId = new ObjectId();
        UserTimelineEntry entry = new UserTimelineEntry(userId, "post123",
                com.epita.repository.entity.UserTimelineEntryAction.LIKE, Instant.now());

        when(userTimelineRepository.findByUserId(userId)).thenReturn(new ArrayList<>(List.of(entry)));

        UserTimelineResponse response = userTimelineService.getUserTimeline(userId);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(1, response.getUserTimeline().size());
        assertEquals("post123", response.getUserTimeline().get(0).getPostId());
    }

    @Test
    public void testHandlePostTimeline_creation_shouldCallCreateEntry() {
        PostTimeline postTimeline = new PostTimeline();
        postTimeline.setUserId(new ObjectId());
        postTimeline.setPostId("post123");
        postTimeline.setMethod("creation");
        postTimeline.setPostModificationDate(Instant.now());

        userTimelineService.handlePostTimeline(postTimeline);

        verify(userTimelineRepository, times(1)).createEntry(any());
    }

    @Test
    public void testHandlePostTimeline_deletion_shouldCallDeleteEntry() {
        PostTimeline postTimeline = new PostTimeline();
        postTimeline.setUserId(new ObjectId());
        postTimeline.setPostId("post123");
        postTimeline.setMethod("deletion");
        postTimeline.setPostModificationDate(Instant.now());

        userTimelineService.handlePostTimeline(postTimeline);

        verify(userTimelineRepository, times(1)).deletePost(any());
    }

    @Test
    public void testHandlePostTimeline_unknownMethod_shouldNotCallRepo() {
        PostTimeline postTimeline = new PostTimeline();
        postTimeline.setUserId(new ObjectId());
        postTimeline.setPostId("post123");
        postTimeline.setMethod("edit");
        postTimeline.setPostModificationDate(Instant.now());

        userTimelineService.handlePostTimeline(postTimeline);

        verify(userTimelineRepository, never()).createEntry(any());
        verify(userTimelineRepository, never()).deleteEntry(any());
    }

    @Test
    public void testHandleLikeTimeline_like_shouldCallCreateEntry() {
        LikeTimeline likeTimeline = new LikeTimeline();
        likeTimeline.setUserId(new ObjectId());
        likeTimeline.setPostId("post123");
        likeTimeline.setMethod("like");
        likeTimeline.setPostLikeDate(LocalDateTime.now());

        userTimelineService.handleLikeTimeline(likeTimeline);

        verify(userTimelineRepository, times(1)).createEntry(any());
    }

    @Test
    public void testHandleLikeTimeline_unlike_shouldCallDeleteEntry() {
        LikeTimeline likeTimeline = new LikeTimeline();
        likeTimeline.setUserId(new ObjectId());
        likeTimeline.setPostId("post123");
        likeTimeline.setMethod("unlike");
        likeTimeline.setPostLikeDate(LocalDateTime.now());

        userTimelineService.handleLikeTimeline(likeTimeline);

        verify(userTimelineRepository, times(1)).deleteEntry(any());
    }

    @Test
    public void testHandleLikeTimeline_unknown_shouldNotCallRepo() {
        LikeTimeline likeTimeline = new LikeTimeline();
        likeTimeline.setUserId(new ObjectId());
        likeTimeline.setPostId("post123");
        likeTimeline.setMethod("meh");
        likeTimeline.setPostLikeDate(LocalDateTime.now());

        userTimelineService.handleLikeTimeline(likeTimeline);

        verify(userTimelineRepository, never()).createEntry(any());
        verify(userTimelineRepository, never()).deleteEntry(any());
    }

    @Test
    public void testGetUserTimeline_shouldReturnEntriesSortedChronologically() {
        ObjectId userId = new ObjectId();

        UserTimelineEntry entry1 = new UserTimelineEntry(userId, "post1",
                com.epita.repository.entity.UserTimelineEntryAction.LIKE, Instant.parse("2025-04-07T12:00:00Z"));
        UserTimelineEntry entry2 = new UserTimelineEntry(userId, "post2",
                com.epita.repository.entity.UserTimelineEntryAction.LIKE, Instant.parse("2025-04-07T10:00:00Z"));
        UserTimelineEntry entry3 = new UserTimelineEntry(userId, "post3",
                com.epita.repository.entity.UserTimelineEntryAction.LIKE, Instant.parse("2025-04-07T11:00:00Z"));

        when(userTimelineRepository.findByUserId(userId)).thenReturn(new ArrayList<>(List.of(entry1, entry2, entry3)));


        UserTimelineResponse response = userTimelineService.getUserTimeline(userId);
        List<String> sortedPostIds = response.getUserTimeline().stream().map(p -> p.getPostId()).toList();

        assertEquals(List.of("post2", "post3", "post1"), sortedPostIds, "Posts should be sorted chronologically by performedAt");
    }

}
