package com.epita;

import com.epita.repository.SentimentRepository;
import com.epita.repository.entity.Sentiment;
import com.epita.repository.restClient.SentimentRestClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class SentimentRepositoryTest {

    @Inject
    SentimentRepository repository;

    @InjectMock
    @RestClient
    SentimentRestClient sentimentRestClient;

    ObjectId randomObjectId = new ObjectId();
    String postId = new ObjectId().toHexString();
    String postContent = "What a wonderful day!";
    String sentimentLabel = "positive";

    @BeforeEach
    public void setup() {
        repository.deleteSentiment(postId); // Clean slate before each test
    }

    @Test
    public void testCreateAndFindSentiment() {
        Sentiment sentiment = new Sentiment(randomObjectId, new ObjectId(postId), postContent, sentimentLabel);
        repository.createSentiment(sentiment);

        Sentiment result = repository.findByPostId(postId);

        assertNotNull(result);
        assertEquals(postContent, result.getContent());
        assertEquals(sentimentLabel, result.getSentiment());
    }

    @Test
    public void testDeleteSentiment() {
        Sentiment sentiment = new Sentiment(randomObjectId, new ObjectId(postId), postContent, sentimentLabel);
        repository.createSentiment(sentiment);

        Sentiment result = repository.findByPostId(postId);

        assertNotNull(result);

        repository.deleteSentiment(postId);
        result = repository.findByPostId(postId);

        assertNull(result);
    }
}
