package com.epita;

import com.epita.repository.SentimentRepository;
import com.epita.repository.entity.Sentiment;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class SentimentControllerTest {

    @Inject
    SentimentRepository sentimentRepository;

    String postId = new ObjectId().toHexString();
    String sentiment = "positive";

    @BeforeEach
    public void setup() {
        sentimentRepository.deleteSentiment(postId);
        Sentiment s = new Sentiment(new ObjectId(), new ObjectId(postId), "Sun is shining", sentiment);
        sentimentRepository.createSentiment(s);
    }

    @Test
    public void testGetPostSentimentSuccess() {
        given()
                .when()
                .get("/api/sentiment/getPostSentiment/" + postId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("postId", is(postId))
                .body("sentiment", is(sentiment));
    }

    @Test
    public void testGetPostSentimentBadRequestWrongFormat() {
        given()
                .when()
                .get("/api/sentiment/getPostSentiment/unknownId")
                .then()
                .statusCode(400);
    }

    @Test
    public void testGetPostSentimentUnknownId() {
        ObjectId randObjectId = new ObjectId();

        given()
                .when()
                .get("/api/sentiment/getPostSentiment/" + randObjectId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetPostSentimentBadRequest() {
        given()
                .when()
                .get("/api/sentiment/getPostSentiment/")
                .then()
                .statusCode(404);
    }
}
