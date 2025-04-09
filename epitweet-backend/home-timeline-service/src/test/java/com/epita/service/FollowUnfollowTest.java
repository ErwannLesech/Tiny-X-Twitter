package com.epita.service;

import com.epita.contracts.post.PostResponse;
import com.epita.contracts.social.BlockedRelationRequest;
import com.epita.contracts.social.BlockedRelationResponse;
import com.epita.controller.contracts.HomeTimelinePost;
import com.epita.controller.contracts.HomeTimelineResponse;
import com.epita.payloads.homeTimeline.SocialHomeTimelineBlock;
import com.epita.payloads.homeTimeline.SocialHomeTimelineFollow;
import com.epita.repository.HomeTimelineRepository;
import com.epita.repository.entity.EntryType;
import com.epita.repository.entity.HomeTimelineEntry;
import com.epita.repository.restClient.PostRestClient;
import com.epita.repository.restClient.SocialRestClient;
import groovyjarjarpicocli.CommandLine;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
public class FollowUnfollowTest {
    @Inject
    HomeTimelineService homeTimelineService;

    @Inject
    HomeTimelineRepository homeTimelineRepository;

    @InjectMock
    @RestClient
    PostRestClient postRestClient;

    @InjectMock
    @RestClient
    SocialRestClient socialRestClient;

    // User123 will follow User456
    String user123Id = "a00000000000000000000123";
    String user456Id = "a00000000000000000000456";

    // Post created by User456
    String post123Id = "a00000000000000000000123";

    @BeforeEach
    public void setup() {
        homeTimelineRepository.deleteAll();
    }

    /**
     * Test when a user follow another user and check if the timeline updated properly
     */
    @Test
    public void followHomeEntry() {
        // Response excepted at the end of the test
        HomeTimelinePost exceptedPost = new HomeTimelinePost();
        exceptedPost.setUserId(new ObjectId(user456Id));
        exceptedPost.setPostId(new ObjectId(post123Id));
        exceptedPost.setType(EntryType.POST);
        HomeTimelineResponse excepted = new HomeTimelineResponse(new ObjectId(user123Id), List.of(exceptedPost));

        // Message catch by SocialFollowSubscriber when user1 follow user2
        SocialHomeTimelineFollow message = new SocialHomeTimelineFollow();
        message.setMethod("follow");
        message.setUserId(new ObjectId(user123Id));
        message.setUserFollowedId(new ObjectId(user456Id));

        // The response excepted when using the post Rest Client
        PostResponse post = new PostResponse();
        post.set_id(new ObjectId(post123Id));
        RestResponse<List<PostResponse>> res = RestResponse.ok(List.of(post));

        // The response excepted when using social Rest Client
        BlockedRelationResponse blockRes = new BlockedRelationResponse(false, false);
        RestResponse<BlockedRelationResponse> blockedRes = RestResponse.ok(blockRes);

        // Force the return values of the rest clients
        when(postRestClient.getPosts(any(ObjectId.class))).thenReturn(res);
        when(socialRestClient.getBlockedRelation(any(BlockedRelationRequest.class))).thenReturn(blockedRes);

        // Method to test
        homeTimelineService.updateOnFollow(message);

        HomeTimelineResponse timeline = homeTimelineService.getHomeTimeline(new ObjectId(user123Id));

        assertEquals(excepted.getTimeline().size(), timeline.getTimeline().size());
        assertEquals(excepted.getUserId(), timeline.getUserId());
        assertEquals(excepted.getTimeline().get(0).getUserId(), timeline.getTimeline().get(0).getUserId());
        assertEquals(excepted.getTimeline().get(0).getPostId(), timeline.getTimeline().get(0).getPostId());
        assertEquals(excepted.getTimeline().get(0).getType(), timeline.getTimeline().get(0).getType());
    }

    /**
     * Test when a user unfollow another user and check if the timeline updated properly
     */
    @Test
    public void unfollowHomeEntry() {
        // Add an entry on the database.
        HomeTimelineEntry entry = new HomeTimelineEntry();
        entry.setUserId(new ObjectId(user123Id));
        entry.setUserFollowedId(new ObjectId(user456Id));
        entry.setPostId(new ObjectId(post123Id));
        entry.setPostType(EntryType.POST);
        entry.setDate(Instant.parse("2028-01-01T00:00:00Z"));
        homeTimelineRepository.addHomeEntry(entry);

        // Message catch by SocialFollowSubscriber when user1 unfollow user2
        SocialHomeTimelineFollow message = new SocialHomeTimelineFollow();
        message.setMethod("unfollow");
        message.setUserId(new ObjectId(user123Id));
        message.setUserFollowedId(new ObjectId(user456Id));

        // The response excepted when using the post Rest Client
        PostResponse post = new PostResponse();
        post.set_id(new ObjectId(post123Id));
        RestResponse<List<PostResponse>> res = RestResponse.ok(List.of(post));

        // The response excepted when using social Rest Client
        BlockedRelationResponse blockRes = new BlockedRelationResponse(false, false);
        RestResponse<BlockedRelationResponse> blockedRes = RestResponse.ok(blockRes);

        // Force the return values of the rest clients
        when(postRestClient.getPosts(any(ObjectId.class))).thenReturn(res);
        when(socialRestClient.getBlockedRelation(any(BlockedRelationRequest.class))).thenReturn(blockedRes);

        homeTimelineService.updateOnFollow(message);

        HomeTimelineResponse timeline = homeTimelineService.getHomeTimeline(new ObjectId(user123Id));

        assertEquals(0, timeline.getTimeline().size());
    }
}
