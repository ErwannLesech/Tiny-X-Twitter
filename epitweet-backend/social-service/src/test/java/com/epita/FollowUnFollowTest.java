package com.epita;

import com.epita.controller.contracts.FollowUnfollowRequest;
import com.epita.repository.SocialRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class FollowUnFollowTest
{
    private static final Logger LOG = Logger.getLogger(SocialRepository.class);
    @Inject
    SocialRepository socialRepository;

    /**
     * clean neo4j repository and append users
     */
    @BeforeEach
    public void cleanAndSetup()
    {
        List<String> usersId = List.of(
            "user123",
            "user456",
            "user789",
            "user999");
        socialRepository.clean();
        socialRepository.createUser(usersId);
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
            "unknown",
            "user123");

        given().contentType(ContentType.JSON)
            .body(followUnfollowRequest)
            .when()
            .post("/api/social/follow")
            .then()
            .statusCode(400);

        List<String> followsId = socialRepository.getFollows("user123");
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
            "user456",
            "unknown");

        given().contentType(ContentType.JSON)
            .body(followUnfollowRequest)
            .when()
            .post("/api/social/follow")
            .then()
            .statusCode(400);

        List<String> followsId = socialRepository.getFollowers("user456");
        List<String> expectedFollowsId = List.of();
        testResult(followsId, expectedFollowsId);
    }

    /**
     * TEST follow ErrorInvalidBody:<br>
     *  no body
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
            "user456",
            "user123");

        given().contentType(ContentType.JSON)
            .body(followUnfollowRequest)
            .when()
            .post("/api/social/follow")
            .then()
            .statusCode(200);

        List<String> followsId = socialRepository.getFollows("user123");
        List<String> expectedFollowsId = List.of("user456");
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
            "user789",
            "user123");

        given().contentType(ContentType.JSON)
            .body(followUnfollowRequest)
            .when()
            .post("/api/social/follow")
            .then()
            .statusCode(200);

        List<String> followsId = socialRepository.getFollows("user123");
        List<String> expectedFollowsId = List.of("user456", "user789");
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
            "user456",
            "user123");

        given().contentType(ContentType.JSON)
            .body(followUnfollowRequest)
            .when()
            .post("/api/social/follow")
            .then()
            .statusCode(200);

        List<String> followsId = socialRepository.getFollows("user123");
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
            .get("/api/social/getFollows/unknown")
            .then()
            .statusCode(400);
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
            .get("/api/social/getFollows/user123")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> expectedFollowsId = List.of("user456", "user789");
        testResult(followsId, expectedFollowsId);
    }

    /**
     * TEST get followers ErrorUnknownUser
     */
    @Test void testGetFollowersErrorUnknownUser()
    {
        given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getFollowers/unknown")
            .then()
            .statusCode(400);
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
            .get("/api/social/getFollowers/user456")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> followersId789 = given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getFollowers/user789")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> expectedFollowsId = List.of("user123");
        testResult(followersId456, expectedFollowsId);
        testResult(followersId789, expectedFollowsId);
    }
}