package com.epita;

import com.epita.controller.contracts.AppreciationRequest;
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
public class LikeUnlikeTest
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
            "user999"
        );
        List<String> postsId = List.of(
            "post123",
            "post456",
            "post789",
            "post999"
        );
        socialRepository.clean();
        socialRepository.createResource(usersId, SocialRepository.TypeCreate.USER);
        socialRepository.createResource(postsId, SocialRepository.TypeCreate.POST);
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
     * TEST like ErrorUnknownUser:<br>
     *  user123 like unknown
     */
    @Test
    public void testLikeErrorUnknownUser()
    {
        AppreciationRequest appreciationRequest = new AppreciationRequest(
            true,
            "unknown",
            "user123");

        given().contentType(ContentType.JSON)
            .body(appreciationRequest)
            .when()
            .post("/api/social/like")
            .then()
            .statusCode(404);

        List<String> likePostsId = socialRepository.getLikesPosts("user123");
        List<String> expectedLikesId = List.of();
        testResult(likePostsId, expectedLikesId);
    }

    /**
     * TEST like ErrorUnknownUser2:<br>
     *  unknown like post123
     */
    @Test
    public void testLikeErrorUnknownUser2()
    {
        AppreciationRequest appreciationRequest = new AppreciationRequest(
            true,
            "post123",
            "unknown");

        given().contentType(ContentType.JSON)
            .body(appreciationRequest)
            .when()
            .post("/api/social/like")
            .then()
            .statusCode(404);

        List<String> likeUsersId = socialRepository.getLikeUsers("post123");
        List<String> expectedLikesId = List.of();
        testResult(likeUsersId, expectedLikesId);
    }

    /**
     * TEST like ErrorInvalidBody:<br>
     *  No Body
     */
    @Test
    public void testLikeErrorInvalidBody()
    {

        given().contentType(ContentType.JSON)
            .when()
            .post("/api/social/like")
            .then()
            .statusCode(400);
    }

    /**
     * TEST like:<br>
     *  user123 like post123
     */
    @Test
    public void testLike()
    {
        AppreciationRequest appreciationRequest  = new AppreciationRequest(
            true,
            "post123",
            "user123");

        given().contentType(ContentType.JSON)
            .body(appreciationRequest)
            .when()
            .post("/api/social/like")
            .then()
            .statusCode(200);

        List<String> likesId = socialRepository.getLikesPosts("user123");
        LOG.info(likesId.toString());
        List<String> expectedLikesId = List.of("post123");
        testResult(likesId, expectedLikesId);
    }

    /**
     * TEST like:<br>
     *  user123 like post123<br>
     *  user123 like post456
     */
    @Test
    public void testMultiLike()
    {
        testLike();

        AppreciationRequest appreciationRequest = new AppreciationRequest(
            true,
            "post456",
            "user123");

        given().contentType(ContentType.JSON)
            .body(appreciationRequest)
            .when()
            .post("/api/social/like")
            .then()
            .statusCode(200);

        List<String> likesId = socialRepository.getLikesPosts("user123");
        List<String> expectedLikesId = List.of("post123", "post456");
        testResult(likesId, expectedLikesId);
    }

    /**
     * TEST unLike:<br>
     * user123 unlike post123<br>
     * After:
     * user123 like post123<br>
     */
    @Test
    public void testUnlike()
    {
        testLike();

        AppreciationRequest appreciationRequest = new AppreciationRequest(
            false,
            "post123",
            "user123");

        given().contentType(ContentType.JSON)
            .body(appreciationRequest)
            .when()
            .post("/api/social/like")
            .then()
            .statusCode(200);

        List<String> likesId = socialRepository.getLikesPosts("user123");
        List<String> expectedLikesId = List.of();
        testResult(likesId, expectedLikesId);
    }

    /**
     * TEST get likePosts ErrorUnknownUser
     */
    @Test void testGetLikePostsErrorUnknownUser()
    {
        given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getLikedPosts/unknown")
            .then()
            .statusCode(404);
    }

    /**
     * TEST get likedPosts for user123 after:<br>
     * TEST like:<br>
     *  user123 like post123<br>
     *  user123 like post456<br>
     */
    @Test
    public void testGetLikedPosts()
    {
        testMultiLike();

        List<String> likedPostsId = given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getLikedPosts/user123")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> expectedLikesId = List.of("post123", "post456");
        testResult(likedPostsId, expectedLikesId);
    }

    /**
     * TEST get likeUsers ErrorUnknownUser
     */
    @Test void testGetLikeUsersErrorUnknownUser()
    {
        given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getLikeUsers/unknown")
            .then()
            .statusCode(404);
    }

    /**
     * TEST get likeUsers for post123 and post456 after:<br>
     * TEST like:<br>
     *  user123 like post123<br>
     *  user123 like post456<br>
     */
    @Test
    public void testGetLikeUsers()
    {
        testMultiLike();

        List<String> likeUserId123 = given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getLikeUsers/post123")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> likeUserId456 = given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getLikeUsers/post456")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> expectedLikesId = List.of("user123");
        testResult(likeUserId123, expectedLikesId);
        testResult(likeUserId456, expectedLikesId);
    }
}