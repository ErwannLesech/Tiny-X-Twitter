package com.epita;

import com.epita.repository.UserRepository;
import com.epita.repository.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class UserControllerTest {

    @Inject
    UserRepository userRepository;

    @Inject
    Logger logger;

    String firstUser = "{ \"tag\": \"group3\", \"pseudo\": \"grp3RPZ\" }";
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

        given().contentType(ContentType.JSON)
                .when()
                .delete("/api/users/delete/group3")
                .then()
                .statusCode(200);

        User user = userRepository.findByTag("group3");
        assert user == null;
    }

    @Test
    public void testDeleteUserNotFound() {
        given().contentType(ContentType.JSON)
                .when()
                .delete("/api/users/delete/nonexistent")
                .then()
                .statusCode(404);
    }
}