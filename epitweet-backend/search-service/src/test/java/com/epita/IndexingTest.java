package com.epita;

import com.epita.controller.contracts.PostRequest;
import com.epita.controller.contracts.PostResponse;
import com.epita.payloads.search.IndexPost;
import com.epita.service.SearchService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
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

    /**
     * Test for indexing posts
     */
/*    @Test
    void a_testIndexRandomPosts() {
        for (int i = 0; i < 5; i++) { // Génère 5 posts aléatoires
            PostRequest post = generateRandomPost();
            assertDoesNotThrow(() -> searchService.indexPost(post));
            generatedPosts.add(post.getId());
            LOGGER.info("Successfully indexed post with ID: " + post.getId());
        }
    }*/

    /**
     * Test for delete indexed posts
     */
/*    @Test
    void b_deletePosts() {
        for (String postId : generatedPosts) {
            assertDoesNotThrow(() ->searchService.deletePost(postId));
            LOGGER.info("Successfully delete post with ID: " + postId);
        }
    }*/

    /**
     * Génère un PostDocument aléatoire avec un UUID
     */
/*    private PostRequest generateRandomPost() {
        List<String> words = List.of("hello", "quarkus", "elasticsearch", "random", "test", "java", "mockito", "#yoyo");
        Random random = new Random();

        String id = UUID.randomUUID().toString();
        String postType = random.nextBoolean() ? "post" : "reply";
        String content = words.get(random.nextInt(words.size())) + " " + words.get(random.nextInt(words.size()));
        String mediaPath = random.nextBoolean() ? "https://example.com/media" + id + ".jpg" : "";
        String parentId = postType.equals("reply") ? UUID.randomUUID().toString() : "";

        return new PostRequest(id, postType, content, mediaPath, parentId);
    }*/



    /**
     * Tests for searching
     */
    @Test
    void c_searchPost_found() {
        IndexPost post = new IndexPost(
                new ObjectId().toString(),
                "post",
                "Bonjour le monde #test",
                "",
                null,
                "creation"
        );

        searchService.indexPost(post);

        List<PostResponse> results = assertDoesNotThrow(() -> searchService.searchPosts("bonjour"));
        assertFalse(results.isEmpty(), "Expected to find at least one result");
        searchService.deletePost(post.getPostId());
    }

    @Test
    void d_searchPost_notFound() {
        List<PostResponse> results = assertDoesNotThrow(() -> searchService.searchPosts("termeintrouvable123"));
        assertTrue(results.isEmpty(), "Expected to find no results");
    }

    @Test
    void e_searchPost_hashtag() {
        IndexPost post = new IndexPost(
                new ObjectId().toString(),
                "post",
                "Ce post parle de #Été2024",
                "",
                null,
                "creation"
        );

        searchService.indexPost(post);

        List<PostResponse> results = assertDoesNotThrow(() -> searchService.searchPosts("#Été2024"));
        assertFalse(results.isEmpty(), "Expected to find post with hashtag #Été2024");

        searchService.deletePost(post.getPostId());
    }

    @Test
    void f_searchPost_caseInsensitive() {
        IndexPost post = new IndexPost(
                new ObjectId().toString(),
                "post",
                "Le Java est puissant",
                "",
                null,
                "creation"
        );

        searchService.indexPost(post);

        List<PostResponse> results = assertDoesNotThrow(() -> searchService.searchPosts("JAVA"));
        assertFalse(results.isEmpty(), "Expected to find result for 'JAVA' despite casing");

        searchService.deletePost(post.getPostId());
    }

    @Test
    void g_searchPost_accentNormalization() {
        IndexPost post = new IndexPost(
                new ObjectId().toString(),
                "post",
                "C'était l'été à São Paulo",
                "",
                null,
                "creation"
        );

        searchService.indexPost(post);

        List<PostResponse> results = assertDoesNotThrow(() -> searchService.searchPosts("ete sao"));
        assertFalse(results.isEmpty(), "Expected to find result with accent-normalized words");

        searchService.deletePost(post.getPostId());
    }

    @Test
    void i_searchPost_multipleTokens() {
        IndexPost post = new IndexPost(
                new ObjectId().toString(),
                "post",
                "java quarkus elasticsearch",
                "",
                null,
                "creation"
        );

        searchService.indexPost(post);

        List<PostResponse> results = assertDoesNotThrow(() -> searchService.searchPosts("quarkus elasticsearch"));
        assertFalse(results.isEmpty(), "Expected to find result matching both terms");

        searchService.deletePost(post.getPostId());
    }

    @Test
    void j_searchPost_multiplePostsAndStrictMatching() {
        List<IndexPost> posts = List.of(
                new IndexPost(new ObjectId().toString(), "post", "This is a #tech post about #java", "", null, "creation"),
                new IndexPost(new ObjectId().toString(), "post", "Exploring #java and #springboot projects", "", null, "creation"),
                new IndexPost(new ObjectId().toString(), "post", "Learning about Java and tech in 2024", "", null, "creation"),
                new IndexPost(new ObjectId().toString(), "post", "#java is awesome", "", null, "creation"),
                new IndexPost(new ObjectId().toString(), "post", "Completely unrelated content", "", null, "creation")
        );

        // Index all posts
        for (IndexPost post : posts) {
            searchService.indexPost(post);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act + Assert 1: Search only for hashtag #java
        List<PostResponse> result1 = searchService.searchPosts("#java");
        assertEquals(3, result1.size(), "Expected 3 posts with #java only");

        // Assert all returned posts actually contain #java (not just "java")
        for (PostResponse post : result1) {
            assertTrue(post.getContent().contains("#java"), "Post must contain #java as hashtag only");
        }

        // Act + Assert 2: Search for regular word "java" (should NOT match #java)
        List<PostResponse> result2 = searchService.searchPosts("java");
        assertEquals(1, result2.size(), "Expected only 1 post with word 'java' not hashtag");

        for (PostResponse post : result2) {
            assertTrue(post.getContent().contains("Java") && !post.getContent().contains("#java"),
                    "Must match only plain 'java', not #java");
        }

        // Act + Assert 3: Search for both word 'tech' and hashtag #java
        List<PostResponse> result3 = searchService.searchPosts("tech #java");
        assertEquals(1, result3.size(), "Only one post should contain both word 'tech' and hashtag #java");

        PostResponse matchedPost = result3.get(0);
        assertTrue(matchedPost.getContent().contains("tech"), "Should contain 'tech' as plain word");
        assertTrue(matchedPost.getContent().contains("#java"), "Should contain #java");

        // Act + Assert 4: Search for two hashtags together #java #springboot
        List<PostResponse> result4 = searchService.searchPosts("#java #springboot");
        assertEquals(1, result4.size(), "Only one post should match both hashtags");

        PostResponse hashtagMatch = result4.get(0);
        assertTrue(hashtagMatch.getContent().contains("#java"), "Should contain #java");
        assertTrue(hashtagMatch.getContent().contains("#springboot"), "Should contain #springboot");

        // Cleanup
        for (IndexPost post : posts) {
            searchService.deletePost(post.getPostId());
        }
    }
}
