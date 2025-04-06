package com.epita;

import com.epita.controller.contracts.AppreciationRequest;
import com.epita.repository.PostRestClient;
import com.epita.repository.SocialRepository;
import com.epita.repository.UserRestClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
public class LikeUnlikeTest
{
    private static final Logger LOG = Logger.getLogger(SocialRepository.class);
    @Inject
    SocialRepository socialRepository;

    @InjectMock
    @RestClient
    UserRestClient userRestClient;

    @InjectMock
    @RestClient
    PostRestClient postRestClient;

    String user123Id = "a00000000000000000000123";
    String user456Id = "a00000000000000000000456";
    String user789Id = "a00000000000000000000789";
    String post123Id = "a00000000000000000000123";
    String post456Id = "a00000000000000000000456";
    String post789Id = "a00000000000000000000789";
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
        List<String> postsId = List.of(
            post123Id,
            post456Id,
            post789Id
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
        when(postRestClient.getPost(any(ObjectId.class))).thenAnswer(invocation -> {
            ObjectId postId = invocation.getArgument(0);
            if (postsId.contains(postId.toHexString())) {
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
     * TEST like ErrorUnknownUser:<br>
     *  user123 like unknown
     */
    @Test
    public void testLikeErrorUnknownUser()
    {
        AppreciationRequest appreciationRequest = new AppreciationRequest(
            true,
            unknownId,
            user123Id);

        given().contentType(ContentType.JSON)
            .body(appreciationRequest)
            .when()
            .post("/api/social/like")
            .then()
            .statusCode(404);

        List<String> likePostsId = socialRepository.getLikesPosts(user123Id);
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
            post123Id,
            unknownId);

        given().contentType(ContentType.JSON)
            .body(appreciationRequest)
            .when()
            .post("/api/social/like")
            .then()
            .statusCode(404);

        List<String> likeUsersId = socialRepository.getLikeUsers(post123Id);
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
            post123Id,
            user123Id);

        given().contentType(ContentType.JSON)
            .body(appreciationRequest)
            .when()
            .post("/api/social/like")
            .then()
            .statusCode(200);

        List<String> likesId = socialRepository.getLikesPosts(user123Id);
        LOG.info(likesId.toString());
        List<String> expectedLikesId = List.of(post123Id);
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
            post456Id,
            user123Id);

        given().contentType(ContentType.JSON)
            .body(appreciationRequest)
            .when()
            .post("/api/social/like")
            .then()
            .statusCode(200);

        List<String> likesId = socialRepository.getLikesPosts(user123Id);
        List<String> expectedLikesId = List.of(post123Id, post456Id);
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
            post123Id,
            user123Id);

        given().contentType(ContentType.JSON)
            .body(appreciationRequest)
            .when()
            .post("/api/social/like")
            .then()
            .statusCode(200);

        List<String> likesId = socialRepository.getLikesPosts(user123Id);
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
            .get("/api/social/getLikedPosts/" + unknownId)
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
            .get("/api/social/getLikedPosts/" + user123Id)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> expectedLikesId = List.of(post123Id, post456Id);
        testResult(likedPostsId, expectedLikesId);
    }

    /**
     * TEST get likeUsers ErrorUnknownUser
     */
    @Test void testGetLikeUsersErrorUnknownUser()
    {
        given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getLikeUsers/" + unknownId)
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
            .get("/api/social/getLikeUsers/" + post123Id)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> likeUserId456 = given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getLikeUsers/" + post456Id)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> expectedLikesId = List.of(user123Id);
        testResult(likeUserId123, expectedLikesId);
        testResult(likeUserId456, expectedLikesId);
    }
}