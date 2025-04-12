package com.epita.service;

import com.epita.controller.contracts.HomeTimelinePost;
import com.epita.controller.contracts.HomeTimelineResponse;
import com.epita.repository.HomeTimelineRepository;
import com.epita.repository.entity.EntryType;
import com.epita.repository.entity.HomeTimelineEntry;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class BasisTest {
    @Inject
    HomeTimelineService homeTimelineService;

    @Inject
    HomeTimelineRepository homeTimelineRepository;


    String user123Id = "a00000000000000000000123";
    String post123Id = "a00000000000000000000123";
    String post456Id = "a00000000000000000000456";

    @BeforeEach
    public void setup() {
        homeTimelineRepository.deleteAll();

        HomeTimelineEntry entry = new HomeTimelineEntry();
        entry.setUserId(new ObjectId(user123Id));
        entry.setPostId(new ObjectId(post456Id));
        entry.setPostType(EntryType.POST);
        entry.setDate(Instant.parse("2028-01-01T00:00:00Z"));
        homeTimelineRepository.addHomeEntry(entry);

        entry = new HomeTimelineEntry();
        entry.setUserId(new ObjectId(user123Id));
        entry.setPostId(new ObjectId(post456Id));
        entry.setPostType(EntryType.LIKE);
        entry.setDate(Instant.parse("2028-08-01T00:00:00Z"));
        homeTimelineRepository.addHomeEntry(entry);

        entry = new HomeTimelineEntry();
        entry.setUserId(new ObjectId(user123Id));
        entry.setPostId(new ObjectId(post123Id));
        entry.setPostType(EntryType.LIKE);
        entry.setDate(Instant.parse("2024-08-01T00:00:00Z"));
        homeTimelineRepository.addHomeEntry(entry);
    }

    @Test
    public void getTimelineTest() {
        HomeTimelineResponse timeline = homeTimelineService.getHomeTimeline(new ObjectId(user123Id));
        System.out.println(timeline.getTimeline().stream().map(HomeTimelinePost::getPostOrLikeTime).toList());
        assertEquals(3, timeline.getTimeline().size());
        assertEquals(post123Id, timeline.getTimeline().get(0).getPostId().toString());
        assertEquals(post456Id, timeline.getTimeline().get(1).getPostId().toString());
    }
}
