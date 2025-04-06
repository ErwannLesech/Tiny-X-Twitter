package com.epita;

import com.epita.controller.contracts.PostDocument;
import com.epita.payloads.search.IndexPost;
import com.epita.repository.SearchRepository;
import com.epita.service.SearchService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class IndexSearchingTest {
    List<String> generatedPosts = new ArrayList<>();

    @Inject
    SearchService searchService; // Remplace par le vrai service qui gère Elasticsearch

    @Inject
    SearchRepository searchRepository;

    private static final Logger LOGGER = Logger.getLogger(IndexSearchingTest.class.getName());

    private List<ObjectId> allPostsIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        LOGGER.info("Cleaning up indexed posts"+ allPostsIds.toString());

        for (ObjectId id : allPostsIds) {
            searchService.deletePost(id.toString());
        }

        LOGGER.info("Cleanup completed.");
    }

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
        allPostsIds.add(new ObjectId(post.getPostId()));

        try {
            Thread.sleep(1000); // 1000 milliseconds = 2 seconds
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<PostDocument> results = assertDoesNotThrow(() -> searchRepository.search(searchService.tokenizeText("Bonjour")));
        assertFalse(results.isEmpty(), "Expected to find at least one result");
    }

    @Test
    void d_searchPost_notFound() {
        List<PostDocument> results = assertDoesNotThrow(() -> searchRepository.search(searchService.tokenizeText("termeintrouvable123")));
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
        allPostsIds.add(new ObjectId(post.getPostId()));

        try {
            Thread.sleep(1000); // 1000 milliseconds = 2 seconds
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<PostDocument> results = assertDoesNotThrow(() -> searchRepository.search(searchService.tokenizeText("#Été2024")));
        assertFalse(results.isEmpty(), "Expected to find post with hashtag #Été2024");
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
        allPostsIds.add(new ObjectId(post.getPostId()));

        try {
            Thread.sleep(1000); // 1000 milliseconds = 2 seconds
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<PostDocument> results = assertDoesNotThrow(() -> searchRepository.search(searchService.tokenizeText("JAVA")));
        assertFalse(results.isEmpty(), "Expected to find result for 'JAVA' despite casing");
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
        allPostsIds.add(new ObjectId(post.getPostId()));

        try {
            Thread.sleep(1000); // 1000 milliseconds = 2 seconds
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<PostDocument> results = assertDoesNotThrow(() -> searchRepository.search(searchService.tokenizeText("ete sao")));
        assertFalse(results.isEmpty(), "Expected to find result with accent-normalized words");
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
        allPostsIds.add(new ObjectId(post.getPostId()));

        try {
            Thread.sleep(1000); // 1000 milliseconds = 2 seconds
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<PostDocument> results = assertDoesNotThrow(() -> searchRepository.search(searchService.tokenizeText("quarkus elasticsearch")));
        assertFalse(results.isEmpty(), "Expected to find result matching both terms");
    }

    @Test
    void j_searchPost_multiplePostsAndStrictMatching() {
        List<IndexPost> posts = List.of(
                new IndexPost(new ObjectId().toString(), "post", "This is a #tech post about #java", "", null, "creation"),
                new IndexPost(new ObjectId().toString(), "post", "Exploring #java and #springboot projects", "", null, "creation"),
                new IndexPost(new ObjectId().toString(), "post", "Learning about Java and tech in 2024", "", null, "creation"),
                new IndexPost(new ObjectId().toString(), "post", "#java is awesome", "", null, "creation"),
                new IndexPost(new ObjectId().toString(), "post", "Completely unrelated content", "", null, "creation"),
                new IndexPost(new ObjectId().toString(), "post", "This is a tech conference #java", "", null, "creation")
        );

        // Index all posts
        for (IndexPost post : posts) {
            searchService.indexPost(post);
            allPostsIds.add(new ObjectId(post.getPostId()));
        }

        try {
            Thread.sleep(1000); // 1000 milliseconds = 1 seconds
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Act + Assert 1: Search only for hashtag #java
        List<PostDocument> result1 = searchRepository.search(searchService.tokenizeText("#java"));
        assertEquals(4, result1.size(), "Expected 4 posts with #java only");

        // Assert all returned posts actually contain #java (not just "java")
        for (PostDocument post : result1) {
            assertTrue(post.getPostId().equals(posts.get(0).getPostId()) || post.getPostId().equals(posts.get(1).getPostId()) || post.getPostId().equals(posts.get(3).getPostId()) || post.getPostId().equals(posts.get(5).getPostId()), "Post must contain #java as hashtag only");
        }

        // Act + Assert 2: Search for regular word "java" (should NOT match #java)
        List<PostDocument> result2 = searchRepository.search(searchService.tokenizeText("java"));
        assertEquals(1, result2.size(), "Expected only 1 post with word 'java' not hashtag");

        for (PostDocument post : result2) {
            assertEquals(post.getPostId(), posts.get(2).getPostId(), "Must match only plain 'java', not #java");
        }

        // Act + Assert 3: Search for both word 'tech' and hashtag #java
        List<PostDocument> result3 = searchRepository.search(searchService.tokenizeText("tech #java"));
        assertEquals(1, result3.size(), "Only one post should contain both word 'tech' and hashtag #java");

        // Act + Assert 4: Search for two hashtags together #java #springboot
        List<PostDocument> result4 = searchRepository.search(searchService.tokenizeText("#java #springboot"));
        assertEquals(1, result4.size(), "Only one post should match both hashtags");

        PostDocument hashtagMatch = result4.get(0);
        assertTrue(hashtagMatch.getPostId().equals(posts.get(0).getPostId()) || hashtagMatch.getPostId().equals(posts.get(1).getPostId()) || hashtagMatch.getPostId().equals(posts.get(3).getPostId()), "Should contain #java");
        assertEquals(hashtagMatch.getPostId(), posts.get(1).getPostId(), "Should contain #springboot");
    }
}
