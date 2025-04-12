package com.epita.service;

import com.epita.contracts.post.PostResponse;
import com.epita.contracts.social.BlockedRelationRequest;
import com.epita.contracts.social.BlockedRelationResponse;
import com.epita.controller.contracts.HomeTimelinePost;
import com.epita.controller.contracts.HomeTimelineResponse;
import com.epita.payloads.homeTimeline.PostHomeTimeline;
import com.epita.payloads.homeTimeline.SocialHomeTimelineFollow;
import com.epita.repository.HomeTimelineRepository;
import com.epita.repository.entity.EntryType;
import com.epita.repository.entity.HomeTimelineEntry;
import com.epita.repository.restClient.PostRestClient;
import com.epita.repository.restClient.SocialRestClient;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
public class PostUnpostTest {
    @Inject
    HomeTimelineService homeTimelineService;

    @Inject
    HomeTimelineRepository homeTimelineRepository;

    @InjectMock
    @RestClient
    SocialRestClient socialRestClient;

    // User123 blocked User456
    String user123Id = "a00000000000000000000123";
    String user456Id = "a00000000000000000000456";

    // Post liked by User123 (before block), create by User456
    String post123Id = "a00000000000000000000123";

    @BeforeEach
    public void setup() {
        homeTimelineRepository.deleteAll();
    }

    @Test
    public void user2postTest() {
        // Response excepted at the end of the test
        HomeTimelinePost exceptedPost = new HomeTimelinePost();
        exceptedPost.setUserId(new ObjectId(user456Id));
        exceptedPost.setPostId(new ObjectId(post123Id));
        exceptedPost.setType(EntryType.POST);
        HomeTimelineResponse excepted = new HomeTimelineResponse(new ObjectId(user123Id), List.of(exceptedPost));

        // The response excepted when using the post Rest Client
        PostResponse post = new PostResponse();
        post.set_id(new ObjectId(post123Id));
        post.setUserId(new ObjectId(user456Id));

        // Message catch by PostSubscriber when user2 followed by user1 create a post
        PostHomeTimeline message = new PostHomeTimeline();
        message.setMethod("creation");
        message.setPost(post);

        List<String> followersId = List.of(user123Id);
        RestResponse<List<String>> followersRes = RestResponse.ok(followersId);

        // Force the return values of the rest clients
        when(socialRestClient.getFollowers(anyString())).thenReturn(followersRes);

        // Method to test
        homeTimelineService.updateOnPost(message);

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

        // The response excepted when using the post Rest Client
        PostResponse post = new PostResponse();
        post.set_id(new ObjectId(post123Id));
        post.setUserId(new ObjectId(user456Id));

        // Message catch by PostSubscriber when user2 followed by user1 create a post
        PostHomeTimeline message = new PostHomeTimeline();
        message.setMethod("deletion");
        message.setPost(post);

        List<String> followersId = List.of(user123Id);
        RestResponse<List<String>> followersRes = RestResponse.ok(followersId);

        // Force the return values of the rest clients
        when(socialRestClient.getFollowers(anyString())).thenReturn(followersRes);

        // Method to test
        homeTimelineService.updateOnPost(message);

        HomeTimelineResponse timeline = homeTimelineService.getHomeTimeline(new ObjectId(user123Id));

        assertEquals(0, timeline.getTimeline().size());
    }

}
