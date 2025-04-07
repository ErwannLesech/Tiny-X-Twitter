package com.epita;

import com.epita.controller.contracts.BlockUnblockRequest;
import com.epita.repository.SocialRepository;
import com.epita.repository.restClient.UserRestClient;
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
public class BlockUnBlockTest
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
     * TEST block ErrorUnknownUser:<br>
     *  user123 block unknown
     */
    @Test
    public void testBlockErrorUnknownUser()
    {
        BlockUnblockRequest blockUnblockRequest = new BlockUnblockRequest(
            true,
            unknownId,
            user123Id);

        given().contentType(ContentType.JSON)
            .body(blockUnblockRequest)
            .when()
            .post("/api/social/block")
            .then()
            .statusCode(404);

        List<String> blocksId = socialRepository.getBlockedUsers(user123Id);
        List<String> expectedBlocksId = List.of();
        testResult(blocksId, expectedBlocksId);
    }

    /**
     * TEST block ErrorUnknownUser2:<br>
     *  unknown block user456
     */
    @Test
    public void testBlockErrorUnknownUser2()
    {
        BlockUnblockRequest blockUnblockRequest = new BlockUnblockRequest(
            true,
            user456Id,
            unknownId);

        given().contentType(ContentType.JSON)
            .body(blockUnblockRequest)
            .when()
            .post("/api/social/block")
            .then()
            .statusCode(404);

        List<String> blocksId = socialRepository.getUsersWhoBlocked(user456Id);
        List<String> expectedBlocksId = List.of();
        testResult(blocksId, expectedBlocksId);
    }

    /**
     * TEST block ErrorInvalidBody:<br>
     *  No Body
     */
    @Test
    public void testBlockErrorInvalidBody()
    {

        given().contentType(ContentType.JSON)
            .when()
            .post("/api/social/block")
            .then()
            .statusCode(400);
    }

    /**
     * TEST block:<br>
     *  user123 block user456
     */
    @Test
    public void testBlock()
    {
        BlockUnblockRequest blockUnblockRequest = new BlockUnblockRequest(
            true,
            user456Id,
            user123Id);

        given().contentType(ContentType.JSON)
            .body(blockUnblockRequest)
            .when()
            .post("/api/social/block")
            .then()
            .statusCode(200);

        List<String> blocksId = socialRepository.getBlockedUsers(user123Id);
        List<String> expectedBlocksId = List.of(user456Id);
        testResult(blocksId, expectedBlocksId);
    }

    /**
     * TEST block:<br>
     *  user123 block user456<br>
     *  user123 block user789
     */
    @Test
    public void testMultiBlock()
    {
        testBlock();

        BlockUnblockRequest blockUnblockRequest = new BlockUnblockRequest(
            true,
            user789Id,
            user123Id);

        given().contentType(ContentType.JSON)
            .body(blockUnblockRequest)
            .when()
            .post("/api/social/block")
            .then()
            .statusCode(200);

        List<String> blocksId = socialRepository.getBlockedUsers(user123Id);
        List<String> expectedBlocksId = List.of(user456Id, user789Id);
        testResult(blocksId, expectedBlocksId);
    }

    /**
     * TEST unBlock:<br>
     * user123 unblock user456<br>
     * After:
     * user123 block user456<br>
     */
    @Test
    public void testUnblock()
    {
        testBlock();

        BlockUnblockRequest blockUnblockRequest = new BlockUnblockRequest(
            false,
            user456Id,
            user123Id);

        given().contentType(ContentType.JSON)
            .body(blockUnblockRequest)
            .when()
            .post("/api/social/block")
            .then()
            .statusCode(200);

        List<String> blocksId = socialRepository.getBlockedUsers(user123Id);
        List<String> expectedBlocksId = List.of();
        testResult(blocksId, expectedBlocksId);
    }

    /**
     * TEST get blocked ErrorUnknownUser
     */
    @Test void testGetBlockedErrorUnknownUser()
    {
        given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getBlocked/" + unknownId)
            .then()
            .statusCode(404);
    }

    /**
     * TEST get blocked for user123 after:<br>
     * TEST block:<br>
     *  user123 block user456<br>
     *  user123 block user789<br>
     */
    @Test
    public void testGetBlocked()
    {
        testMultiBlock();

        List<String> blocksId = given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getBlocked/" + user123Id)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> expectedBlocksId = List.of(user456Id, user789Id);
        testResult(blocksId, expectedBlocksId);
    }

    /**
     * TEST get block ErrorUnknownUser
     */
    @Test void testGetBlockErrorUnknownUser()
    {
        given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getBlock/" + unknownId)
            .then()
            .statusCode(404);
    }

    /**
     * TEST get block for user456 and user789 after:<br>
     * TEST block:<br>
     *  user123 block user456<br>
     *  user123 block user789<br>
     */
    @Test
    public void testGetBlock()
    {
        testMultiBlock();

        List<String> blockersId456 = given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getBlock/" + user456Id)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> blockersId789 = given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getBlock/" + user789Id)
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> expectedBlocksId = List.of(user123Id);
        testResult(blockersId456, expectedBlocksId);
        testResult(blockersId789, expectedBlocksId);
    }
}