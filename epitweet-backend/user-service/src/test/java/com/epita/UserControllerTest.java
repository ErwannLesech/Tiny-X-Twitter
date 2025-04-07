package com.epita;

import com.epita.controller.contracts.UserResponse;
import com.epita.repository.UserRepository;
import com.epita.repository.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.LinkedList;


import static io.restassured.RestAssured.given;

@QuarkusTest
public class UserControllerTest {
    @Inject
    UserRepository userRepository;

    @Inject
    Logger logger;

    String firstUser = "{ \"tag\": \"group3\", \"pseudo\": \"grp3RPZ\", \"password\": \"Incorrect\" }";
    String wrongRequest = "{ \"tag\": \"group3\" }";

    @BeforeEach
    public void setup() {
        userRepository.clear();
    }

    @Test
    public void testCreateUser() {

        given().contentType(ContentType.JSON)
                .body(firstUser)
                .when()
                .post("/api/users/create")
                .then()
                .statusCode(201);

        // Check in db if the user has been created
        User user = userRepository.findByTag("group3");
        assert user != null;
        logger.info(user.toString());
        assert user.pseudo.equals("grp3RPZ");
        assert user.blockedUsers.isEmpty();
    }

    @Test
    public void testCreateUserTagAlreadyExists() {
        given().contentType(ContentType.JSON)
                .body(firstUser)
                .when()
                .post("/api/users/create")
                .then()
                .statusCode(201);

        given().contentType(ContentType.JSON)
                .body(firstUser)
                .when()
                .post("/api/users/create")
                .then()
                .statusCode(409);

        // Check in db if the user is still created
        User user = userRepository.findByTag("group3");
        assert user != null;
        assert user.pseudo.equals("grp3RPZ");
        assert user.blockedUsers.isEmpty();
    }

    @Test
    public void testCreateUserWrongRequest() {

        given().contentType(ContentType.JSON)
                .body(wrongRequest)
                .when()
                .post("/api/users/create")
                .then()
                .statusCode(400);
    }

    @Test
    public void testUpdateUser() {
        // setup user to modify
        User newUser = new User();
        newUser.tag = "group3";
        newUser.pseudo = "grp3RPZ";
        userRepository.createUser(newUser);

        ObjectId firstUserId = userRepository.findByTag("group3")._id;
        String firstUserModification = "{ \"tag\": \"group3\", \"pseudo\": \"grp3RPZLaFamille\", \"blockedUsers\" : [ \"" + firstUserId.toHexString() + "\" ] }";

        given().contentType(ContentType.JSON)
                .body(firstUserModification)
                .when()
                .patch("/api/users/update")
                .then()
                .statusCode(200);

        // Check if the user has been well updated
        User user = userRepository.findById(firstUserId);
        assert user != null;
        assert user.tag.equals("group3"); // tag should not change
        assert user.pseudo.equals("grp3RPZLaFamille");
        assert user.blockedUsers.size() == 1;
        assert user.blockedUsers.contains(firstUserId);
    }


    @Test
    public void testDeleteUser() {
        User newUser = new User();
        newUser.tag = "group3";
        newUser.pseudo = "grp3RPZ";
        userRepository.createUser(newUser);

        UserResponse userResponse = given().contentType(ContentType.JSON)
                .header("userTag", newUser.tag)
                .when()
                .delete("/api/users/delete")
                .then()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        User user = userRepository.findByTag("group3");
        assert user == null;

        assert userResponse != null;
        assert userResponse.get_id() != null;
        assert userResponse.getTag().equals("group3");
        assert userResponse.getPseudo().equals("grp3RPZ");
    }

    @Test
    public void testDeleteUserNotFound() {
        given().contentType(ContentType.JSON)
                .header("userTag", "unknown")
                .when()
                .delete("/api/users/delete")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetUser()
    {
        User newUser = new User();
        newUser.tag = "group3";
        newUser.pseudo = "grp3RPZ";
        newUser.blockedUsers = new LinkedList<>();
        ObjectId randomObjectId = new ObjectId();
        newUser.blockedUsers.add(randomObjectId);
        userRepository.createUser(newUser);

        UserResponse userResponse = given().contentType(ContentType.JSON)
                .header("userTag", newUser.tag)
                .when()
                .get("/api/users/getUser")
                .then()
                .statusCode(200)
                .extract()
                .as(UserResponse.class);

        assert userResponse != null;
        assert userResponse.get_id() != null;
        assert userResponse.getBlockedUsers().size() == 1;
        assert userResponse.getBlockedUsers().contains(randomObjectId);
        assert userResponse.getPseudo().equals("grp3RPZ");
    }

    @Test
    public void testGetUserWrongRequest() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/users/getUser")
                .then()
                .statusCode(400);
    }

    @Test
    public void testGetUserNotFound() {
        given().contentType(ContentType.JSON)
                .header("userTag", "unknown")
                .when()
                .get("/api/users/getUser")
                .then()
                .statusCode(404);
    }

    @Test
    public void testAuthUser()
    {
        given().contentType(ContentType.JSON)
                .body(firstUser)
                .when()
                .post("/api/users/create")
                .then()
                .statusCode(201);

        String authBody = "{ \"tag\": \"group3\", \"pseudo\": \"grp3RPZ\", \"password\": \"Incorrect\" }";


        given().contentType(ContentType.JSON)
                .body(authBody)
                .when()
                .post("/api/users/auth")
                .then()
                .statusCode(200);
    }

    @Test
    public void testAuthUserWrongPassword()
    {
        given().contentType(ContentType.JSON)
                .body(firstUser)
                .when()
                .post("/api/users/create")
                .then()
                .statusCode(201);

        String authBody = "{ \"tag\": \"group3\", \"pseudo\": \"grp3RPZ\", \"password\": \"Correct\" }";


        given().contentType(ContentType.JSON)
                .body(authBody)
                .when()
                .post("/api/users/auth")
                .then()
                .statusCode(401);
    }


    @Test
    public void testAuthUserNotFound()
    {
        given().contentType(ContentType.JSON)
                .body(firstUser)
                .when()
                .post("/api/users/create")
                .then()
                .statusCode(201);

        String authBody = "{ \"tag\": \"group4\", \"pseudo\": \"grp4RPZ\", \"password\": \"Incorrect\" }";


        given().contentType(ContentType.JSON)
                .body(authBody)
                .when()
                .post("/api/users/auth")
                .then()
                .statusCode(404);
    }
}
