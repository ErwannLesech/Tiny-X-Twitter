package com.epita.service;

import com.epita.contracts.post.PostResponse;
import com.epita.controller.contracts.HomeTimelineResponse;
import com.epita.payloads.homeTimeline.SocialHomeTimelineBlock;
import com.epita.repository.HomeTimelineRepository;
import com.epita.repository.entity.EntryType;
import com.epita.repository.entity.HomeTimelineEntry;
import com.epita.repository.restClient.PostRestClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
public class BlockTest {

    @Inject
    HomeTimelineService homeTimelineService;

    @Inject
    HomeTimelineRepository homeTimelineRepository;

    @InjectMock
    @RestClient
    PostRestClient postRestClient;

    // User123 blocked User456
    String user123Id = "a00000000000000000000123";
    String user456Id = "a00000000000000000000456";

    // Post liked by User123 (before block), create by User456
    String post123Id = "a00000000000000000000123";

    @BeforeEach
    public void setup() {
        homeTimelineRepository.deleteAll();

        HomeTimelineEntry entry = new HomeTimelineEntry();
        entry.setUserId(new ObjectId(user123Id));
        entry.setUserFollowedId(new ObjectId(user456Id));
        entry.setPostId(new ObjectId(post123Id));
        entry.setPostType(EntryType.POST);
        homeTimelineRepository.addHomeEntry(entry);
    }

    @Test
    public void user1BlockUser2Test() {
        PostResponse post = new PostResponse();
        post.set_id(new ObjectId(post123Id));
        RestResponse<List<PostResponse>> res = RestResponse.ok(List.of(post));

        when(postRestClient.getPosts(any(ObjectId.class))).thenReturn(res);

        homeTimelineService.updateOnBlock(new SocialHomeTimelineBlock(new ObjectId(user123Id), new ObjectId(user456Id), "block"));

        HomeTimelineResponse timeline = homeTimelineService.getHomeTimeline(new ObjectId(user123Id));

        assertEquals(0, timeline.getTimeline().size());
    }
}
