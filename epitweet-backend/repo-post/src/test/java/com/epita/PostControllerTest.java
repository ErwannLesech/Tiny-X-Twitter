package com.epita;

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
import java.util.UUID;

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
                .header("userId", headerUserFirst)
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
        assert Objects.equals(post.postType.toString(), "post");
        assert post.content.equals(postContent);
        assert post.parentId == null;
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
                .header("userId", headerUserFirst)
                .body(postContent)
                .when()
                .post("/api/posts/createPost")
                .then()
                .statusCode(201);

        // Get the list of posts
        List<Post> posts = given().contentType(ContentType.JSON)
                .header("userId", headerUserFirst)
                .when()
                .get("/api/posts/getPosts")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList("", Post.class);

        assert posts != null;
        assert posts.size() == 1;
        Post post = posts.get(0);
        assert Objects.equals(post.postType.toString(), "post");
        assert post.content.equals(postContent);
        assert post.parentId == null;
    }

    @Test
    public void testGetPost() {
        // Create a post
        given().contentType(ContentType.JSON)
                .body(postContent)
                .when()
                .post("/api/posts/createPost")
                .then()
                .statusCode(201);

        // Get the post by ID
        Post post = given().contentType(ContentType.JSON)
                .when()
                .get("/api/posts/getPost/{postId}", postId)
                .then()
                .statusCode(200)
                .extract().as(Post.class);

        assert post != null;
        assert post._id == postId;
        assert Objects.equals(post.postType.toString(), "post");
        assert post.content.equals(postContent);
    }

    @Test
    public void testGetPostNotFound() {
        ObjectId unknownPostId = new ObjectId();

        given().contentType(ContentType.JSON)
                .when()
                .get("/api/posts/getPost/{postId}", unknownPostId)
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetPostReply() {
        // TODO
    }

    @Test
    public void testGetPostReplyNotFound() {
        // TODO
    }

    @Test
    public void testDeletePost() {
        // TODO
    }

    @Test
    public void testDeletePostNotFound() {
        // TODO
    }

    /***
     * If we arrive to test reply and repost creations by redis queues here,
     * I expect more tests
     **/
}
