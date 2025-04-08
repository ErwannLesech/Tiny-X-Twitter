package com.epita;

import com.epita.controller.contracts.UserTimelinePost;
import com.epita.controller.contracts.UserTimelineResponse;
import com.epita.service.UserTimelineService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

@QuarkusTest
public class UserTimelineControllerTest {

    @InjectMock
    UserTimelineService userTimelineService;

    ObjectId userId = new ObjectId("6606e4c91d4a4d00f32f25df");

    @Test
    public void testGetUserTimeline_validUserId_shouldReturn200() {
        // Mock service response
        UserTimelinePost post = new UserTimelinePost("post_123", "like", Instant.now());
        UserTimelineResponse response = new UserTimelineResponse(userId, List.of(post));

        when(userTimelineService.getUserTimeline(userId)).thenReturn(response);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/timeline/user/" + userId.toHexString())
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }
}
