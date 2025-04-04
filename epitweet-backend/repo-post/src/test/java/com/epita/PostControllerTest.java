package com.epita;

import com.epita.contracts.post.PostResponse;
import com.epita.repository.PostRepository;
import com.epita.repository.entity.Post;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class PostControllerTest {

    @Inject
    PostRepository postRepository;

    @Inject
    Logger logger;

    ObjectId headerUserFirst = new ObjectId();
    String postContent = "{ \"content\": \"This is my first post!\", \"postType\": \"post\" }";
    // I don't know how to test creation of reply/repost as using redis queue that needs user-service to be running
    String postReplyContent = "{ \"content\": \"This is my reply!\", \"postType\": \"reply\", \"parentId\": \"123456\" }";
    String invalidPostContent = "{ \"content\": \"This is an invalid post!\" }";  // Missing 'postType'
    ObjectId postId = new ObjectId();

    @BeforeEach
    public void setup() {
        postRepository.clear();
    }

    @Test
    public void testCreatePost() {
        given().contentType(ContentType.JSON)
                .header("userId", headerUserFirst.toString())
                .body(postContent)
                .when()
                .post("/api/posts/createPost")
                .then()
                .statusCode(201);

        // Check if the post has been created
        List<Post> posts = postRepository.findByUser(headerUserFirst);
        assert posts != null;
        assert posts.size() == 1;

        Post post = posts.get(0);
        assert Objects.equals(post.getPostType().toString(), "post");
        assert post.getContent().equals("This is my first post!");
        assert post.getParentId() == null;
    }

    @Test
    public void testCreatePostInvalidInput() {
        given().contentType(ContentType.JSON)
                .body(invalidPostContent)
                .when()
                .post("/api/posts/createPost")
                .then()
                .statusCode(400);
    }

    @Test
    public void testGetPosts() {
        // Create a post
        given().contentType(ContentType.JSON)
                .header("userId", headerUserFirst.toString())
                .body(postContent)
                .when()
                .post("/api/posts/createPost")
                .then()
                .statusCode(201);

        // Get the list of posts
        List<PostResponse> posts = given().contentType(ContentType.JSON)
                .header("userId", headerUserFirst.toString())
                .when()
                .get("/api/posts/getPosts")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList("", PostResponse.class);

        assert posts != null;
        assert posts.size() == 1;
        PostResponse post = posts.get(0);
        assert Objects.equals(post.postType, "post");
        assert post.content.equals("This is my first post!");
        assert post.parentId == null;
    }

    @Test
    public void testGetPost() {
        // Create a post
        given().contentType(ContentType.JSON)
                .header("userId", headerUserFirst.toString())
                .body(postContent)
                .when()
                .post("/api/posts/createPost")
                .then()
                .statusCode(201);

        postId = postRepository.findByUser(headerUserFirst).get(0).getId();

        PostResponse post = given().contentType(ContentType.JSON)
                .when()
                .get("/api/posts/getPost/" + postId)
                .then()
                .statusCode(200)
                .extract().as(PostResponse.class);

        assert post != null;
        assert post.get_id().toString().equals(postId.toString());
        assert Objects.equals(post.postType, "post");
        assert post.content.equals("This is my first post!");
    }

    @Test
    public void testGetPostNotFound() {
        ObjectId unknownPostId = new ObjectId();

        given().contentType(ContentType.JSON)
                .when()
                .get("/api/posts/getPost/" + unknownPostId)
                .then()
                .statusCode(404);
    }
}
