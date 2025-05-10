package com.epita;

import com.epita.controller.contracts.FollowUnfollowRequest;
import com.epita.repository.SocialRepository;
import com.epita.repository.restClient.UserRestClient;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.mockito.InjectMock;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
public class FollowUnFollowTest
{
    private static final Logger LOG = Logger.getLogger(SocialRepository.class);

    @Inject
    SocialRepository socialRepository;

    @InjectMock
    @RestClient
    UserRestClient userRestClient;

    String user123Id = "a00000000000000000000123";
    String user456Id = "a00000000000000000000456";
    String user789Id = "a00000000000000000000789";
    String unknownId = "a000000000000000000f0f0f";

    /**
     * clean neo4j repository and append users
     */
    @BeforeEach
    public void cleanAndSetup()
    {
        List<String> usersId = List.of(
            user123Id,
            user456Id,
            user789Id
        );
        socialRepository.clean();
        when(userRestClient.getUser(any(ObjectId.class))).thenAnswer(invocation -> {
            ObjectId userId = invocation.getArgument(0);
            if (usersId.contains(userId.toHexString())) {
                return RestResponse.ok();
            } else {
                return RestResponse.status(404);
            }
        });
    }

    /**
     * test if two array are the same in random order
     * @param result the array to test
     * @param expected the expected array
     */
    public void testResult(List<String> result, List<String> expected)
    {
        assert result != null;
        assert result.size() == expected.size();
        List<String> expectedCopy = new ArrayList<>(expected);
        for(String s : result)
        {
            assert expectedCopy.contains(s);
            expectedCopy.remove(s);
        }
        assert expectedCopy.isEmpty();
    }

    /**
     * TEST follow ErrorUnknownUser:<br>
     *  user123 follow unknown
     */
    @Test
    public void testFollowErrorUnknownUser()
    {
        FollowUnfollowRequest followUnfollowRequest = new FollowUnfollowRequest(
            true,
            unknownId,
            user123Id);

        given().contentType(ContentType.JSON)
            .body(followUnfollowRequest)
            .when()
            .post("/api/social/follow")
            .then()
            .statusCode(404);

        List<String> followsId = socialRepository.getFollows(user123Id);
        List<String> expectedFollowsId = List.of();
        testResult(followsId, expectedFollowsId);
    }

    /**
     * TEST follow ErrorUnknownUser2:<br>
     *  unknown follow user456
     */
    @Test
    public void testFollowErrorUnknownUser2()
    {
        FollowUnfollowRequest followUnfollowRequest = new FollowUnfollowRequest(
            true,
            user456Id,
            unknownId);

        given().contentType(ContentType.JSON)
            .body(followUnfollowRequest)
            .when()
            .post("/api/social/follow")
            .then()
            .statusCode(404);

        List<String> followsId = socialRepository.getFollowers(user456Id);
        List<String> expectedFollowsId = List.of();
        testResult(followsId, expectedFollowsId);
    }

    /**
     * TEST follow ErrorInvalidBody:<br>
     *  No Body
     */
    @Test
    public void testFollowErrorInvalidBody()
    {

        given().contentType(ContentType.JSON)
            .when()
            .post("/api/social/follow")
            .then()
            .statusCode(400);
    }

    /**
     * TEST follow:<br>
     *  user123 follow user456
     */
    @Test
    public void testFollow()
    {
        FollowUnfollowRequest followUnfollowRequest = new FollowUnfollowRequest(
            true,
            user456Id,
            user123Id);

        given().contentType(ContentType.JSON)
            .body(followUnfollowRequest)
            .when()
            .post("/api/social/follow")
            .then()
            .statusCode(200);

        List<String> followsId = socialRepository.getFollows(user123Id);
        List<String> expectedFollowsId = List.of(user456Id);
        testResult(followsId, expectedFollowsId);
    }

    /**
     * TEST follow:<br>
     *  user123 follow user456<br>
     *  user123 follow user789
     */
    @Test
    public void testMultiFollow()
    {
        testFollow();

        FollowUnfollowRequest followUnfollowRequest = new FollowUnfollowRequest(
            true,
            user789Id,
            user123Id);

        given().contentType(ContentType.JSON)
            .body(followUnfollowRequest)
            .when()
            .post("/api/social/follow")
            .then()
            .statusCode(200);

        List<String> followsId = socialRepository.getFollows(user123Id);
        List<String> expectedFollowsId = List.of(user456Id, user789Id);
        testResult(followsId, expectedFollowsId);
    }

    /**
     * TEST follow:<br>
     *  user123 follow user456<br>
     *  user123 follow user456
     */
    @Test
    public void testMultiFollowSameUser()
    {
        testFollow();

        FollowUnfollowRequest followUnfollowRequest = new FollowUnfollowRequest(
                true,
                user456Id,
                user123Id);

        given().contentType(ContentType.JSON)
                .body(followUnfollowRequest)
                .when()
                .post("/api/social/follow")
                .then()
                .statusCode(200);

        List<String> followsId = socialRepository.getFollows(user123Id);
        List<String> expectedFollowsId = List.of(user456Id);
        testResult(followsId, expectedFollowsId);
    }

    /**
     * TEST unFollow:<br>
     * user123 unfollow user456<br>
     * After:
     * user123 follow user456<br>
     */
    @Test
    public void testUnfollow()
    {
        testFollow();

        FollowUnfollowRequest followUnfollowRequest = new FollowUnfollowRequest(
            false,
            user456Id,
            user123Id);

        given().contentType(ContentType.JSON)
            .body(followUnfollowRequest)
            .when()
            .post("/api/social/follow")
            .then()
            .statusCode(200);

        List<String> followsId = socialRepository.getFollows(user123Id);
        List<String> expectedFollowsId = List.of();
        testResult(followsId, expectedFollowsId);
    }

    /**
     * TEST get follows ErrorUnknownUser
     */
    @Test void testGetFollowsErrorUnknownUser()
    {
        given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getFollows/" + unknownId)
            .then()
            .statusCode(200);
    }

    /**
     * TEST get follows for user123 after:<br>
     * TEST follow:<br>
     *  user123 follow user456<br>
     *  user123 follow user789<br>
     */
    @Test
    public void testGetFollows()
    {
        testMultiFollow();

        List<String> followsId = given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getFollows/" + user123Id)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> expectedFollowsId = List.of(user456Id, user789Id);
        testResult(followsId, expectedFollowsId);
    }

    /**
     * TEST get followers ErrorUnknownUser
     */
    @Test void testGetFollowersErrorUnknownUser()
    {
        given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getFollowers/" + unknownId)
            .then()
            .statusCode(200);
    }

    /**
     * TEST get followers for user456 and user789 after:<br>
     * TEST follow:<br>
     *  user123 follow user456<br>
     *  user123 follow user789<br>
     */
    @Test
    public void testGetFollowers()
    {
        testMultiFollow();

        List<String> followersId456 = given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getFollowers/" + user456Id)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> followersId789 = given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getFollowers/" + user789Id)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> expectedFollowsId = List.of(user123Id);
        testResult(followersId456, expectedFollowsId);
        testResult(followersId789, expectedFollowsId);
    }
}