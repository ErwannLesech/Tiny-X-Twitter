package com.epita;

import com.epita.contracts.sentiment.SentimentResponse;
import com.epita.payloads.sentiment.AnalysePost;
import com.epita.repository.entity.Sentiment;
import com.epita.converter.SentimentConverter;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SentimentConverterTest {

    @Test
    public void testToResponse() {
        ObjectId _id = new ObjectId();
        ObjectId postId = new ObjectId();
        Sentiment sentiment = new Sentiment(_id, postId, "Good day", "positive");

        SentimentResponse response = SentimentConverter.toResponse(sentiment);

        assertEquals(postId.toString(), response.getPostId());
        assertEquals("positive", response.getSentiment());
    }

    @Test
    public void testToEntity() {
        AnalysePost message = new AnalysePost("507f1f77bcf86cd799439011", "Nice!", "creation");
        Sentiment entity = SentimentConverter.toEntity(message, "positive");

        assertEquals("Nice!", entity.getContent());
        assertEquals("positive", entity.getSentiment());
        assertEquals("507f1f77bcf86cd799439011", entity.getPostId().toHexString());
    }
}
