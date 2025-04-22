package com.epita.converter;

import com.epita.contracts.sentiment.SentimentResponse;
import com.epita.payloads.sentiment.AnalysePost;
import com.epita.payloads.sentiment.AnalyseRequest;
import com.epita.repository.entity.Sentiment;
import org.bson.types.ObjectId;

public class SentimentConverter {

    public static SentimentResponse toResponse(Sentiment sentiment) {
        return new SentimentResponse(
                sentiment.getPostId().toString(),
                sentiment.getSentiment()
        );
    }

    public static Sentiment toEntity(AnalysePost message, String sentiment) {
        return new Sentiment(
                new ObjectId(),
                new ObjectId(message.getPostId()),
                message.getContent(),
                sentiment
        );
    }

    public static AnalyseRequest toAnalyseRequest(String content) {
        return new AnalyseRequest(
                content
        );
    }
}
