package com.epita;

import com.epita.controller.contracts.PostRequest;
import com.epita.service.SearchService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class IndexingTest {
    List<String> generatedPosts = new ArrayList<>();

    @Inject
    SearchService searchService; // Remplace par le vrai service qui gère Elasticsearch

    private static final Logger LOGGER = Logger.getLogger(IndexingTest.class.getName());

    @Test
    void a_testIndexRandomPosts() {
        for (int i = 0; i < 5; i++) { // Génère 5 posts aléatoires
            PostRequest post = generateRandomPost();
            assertDoesNotThrow(() -> searchService.indexPost(post));
            generatedPosts.add(post.getId());
            LOGGER.info("Successfully indexed post with ID: " + post.getId());
        }
    }

    @Test
    void b_deletePosts() {
        for (String postId : generatedPosts) {
            assertDoesNotThrow(() ->searchService.deletePost(postId));
            LOGGER.info("Successfully delete post with ID: " + postId);
        }
    }

    /**
     * Génère un PostDocument aléatoire avec un UUID
     */
    private PostRequest generateRandomPost() {
        List<String> words = List.of("hello", "quarkus", "elasticsearch", "random", "test", "java", "mockito", "#yoyo");
        Random random = new Random();

        String id = UUID.randomUUID().toString();
        String postType = random.nextBoolean() ? "post" : "reply";
        String content = words.get(random.nextInt(words.size())) + " " + words.get(random.nextInt(words.size()));
        String mediaPath = random.nextBoolean() ? "https://example.com/media" + id + ".jpg" : "";
        String parentId = postType.equals("reply") ? UUID.randomUUID().toString() : "";

        return new PostRequest(id, postType, content, mediaPath, parentId);
    }
}
