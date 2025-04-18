package com.epita.service;

import com.epita.contracts.sentiment.SentimentResponse;
import com.epita.contracts.user.UserResponse;
import com.epita.converter.SentimentConverter;
import com.epita.payloads.sentiment.AnalysePost;
import com.epita.repository.SentimentRepository;
import com.epita.repository.entity.Sentiment;
import com.epita.repository.restClient.SentimentRestClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.RestResponse;

public class SentimentService {

    @Inject
    SentimentRepository sentimentRepository;

    @Inject
    @RestClient
    SentimentRestClient sentimentRestClient;

    public SentimentResponse getPostSentiment(String postId) {
        Sentiment sentiment = sentimentRepository.findByPostId(postId);

        if (sentiment == null) {
            return null;
        }

        return SentimentConverter.toResponse(sentiment);
    }

    public void handlePost(AnalysePost message) {
        String method = message.getMethod();

        switch (method) {
            case "creation":
                String sentiment = analysePost(message.getContent());
                sentimentRepository.createSentiment(SentimentConverter.toEntity(message, sentiment));
                break;

            case "deletion":
                sentimentRepository.deleteSentiment(message.getPostId());
                break;

            default:
                break;
        }
    }

    private String analysePost(String content) {

        RestResponse<SentimentResponse> response;

        try {
            response = sentimentRestClient.analyse(content);
            if (response == null || response.getStatus() != 200) {
                return null;
            }
        } catch (ClientWebApplicationException e) {
            return null;
        }

        return response.getEntity().getSentiment();
    }
}
