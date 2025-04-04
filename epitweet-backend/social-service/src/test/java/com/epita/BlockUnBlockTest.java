package com.epita;

import com.epita.controller.contracts.BlockUnblockRequest;
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
public class BlockUnBlockTest
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
     * TEST block ErrorUnknownUser:<br>
     *  user123 block unknown
     */
    @Test
    public void testBlockErrorUnknownUser()
    {
        BlockUnblockRequest blockUnblockRequest = new BlockUnblockRequest(
            true,
            "unknown",
            "user123");

        given().contentType(ContentType.JSON)
            .body(blockUnblockRequest)
            .when()
            .post("/api/social/block")
            .then()
            .statusCode(400);

        List<String> blocksId = socialRepository.getBlockedUsers("user123");
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
            "user456",
            "unknown");

        given().contentType(ContentType.JSON)
            .body(blockUnblockRequest)
            .when()
            .post("/api/social/block")
            .then()
            .statusCode(400);

        List<String> blocksId = socialRepository.getUsersWhoBlocked("user456");
        List<String> expectedBlocksId = List.of();
        testResult(blocksId, expectedBlocksId);
    }

    /**
     * TEST block ErrorInvalidBody:<br>
     *  no body
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
            "user456",
            "user123");

        given().contentType(ContentType.JSON)
            .body(blockUnblockRequest)
            .when()
            .post("/api/social/block")
            .then()
            .statusCode(200);

        List<String> blocksId = socialRepository.getBlockedUsers("user123");
        List<String> expectedBlocksId = List.of("user456");
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
            "user789",
            "user123");

        given().contentType(ContentType.JSON)
            .body(blockUnblockRequest)
            .when()
            .post("/api/social/block")
            .then()
            .statusCode(200);

        List<String> blocksId = socialRepository.getBlockedUsers("user123");
        List<String> expectedBlocksId = List.of("user456", "user789");
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
            "user456",
            "user123");

        given().contentType(ContentType.JSON)
            .body(blockUnblockRequest)
            .when()
            .post("/api/social/block")
            .then()
            .statusCode(200);

        List<String> blocksId = socialRepository.getBlockedUsers("user123");
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
            .get("/api/social/getBlocked/unknown")
            .then()
            .statusCode(400);
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
            .get("/api/social/getBlocked/user123")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> expectedBlocksId = List.of("user456", "user789");
        testResult(blocksId, expectedBlocksId);
    }

    /**
     * TEST get block ErrorUnknownUser
     */
    @Test void testGetBlockErrorUnknownUser()
    {
        given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getBlock/unknown")
            .then()
            .statusCode(400);
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
            .get("/api/social/getBlock/user456")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> blockersId789 = given().contentType(ContentType.JSON)
            .when()
            .get("/api/social/getBlock/user789")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("$", String.class);

        List<String> expectedBlocksId = List.of("user123");
        testResult(blockersId456, expectedBlocksId);
        testResult(blockersId789, expectedBlocksId);
    }
}