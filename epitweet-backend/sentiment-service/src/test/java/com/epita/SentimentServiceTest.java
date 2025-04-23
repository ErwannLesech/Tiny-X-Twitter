package com.epita;

import com.epita.contracts.sentiment.SentimentResponse;
import com.epita.converter.SentimentConverter;
import com.epita.payloads.sentiment.AnalysePost;
import com.epita.payloads.sentiment.AnalyseRequest;
import com.epita.repository.SentimentRepository;
import com.epita.repository.entity.Sentiment;
import com.epita.repository.restClient.SentimentRestClient;
import com.epita.service.SentimentService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class SentimentServiceTest {

    @Inject
    SentimentService sentimentService;

    @InjectMock
    SentimentRepository sentimentRepository;

    @InjectMock
    @RestClient
    SentimentRestClient sentimentRestClient;

    @Test
    public void testGetPostSentiment_shouldReturnSentimentResponse() {
        ObjectId _id = new ObjectId();
        ObjectId postId = new ObjectId();
        Sentiment mockSentiment = new Sentiment(_id, postId, "Life is good", "positive");

        when(sentimentRepository.findByPostId(postId.toHexString())).thenReturn(mockSentiment);

        SentimentResponse response = sentimentService.getPostSentiment(postId.toHexString());

        assertNotNull(response);
        assertEquals(postId.toHexString(), response.getPostId());
        assertEquals("positive", response.getSentiment());
    }

    @Test
    public void testHandlePost_creation_shouldCallCreateSentiment() {
        AnalysePost message = new AnalysePost();
        message.setPostId(new ObjectId().toHexString());
        message.setContent("I love this!");
        message.setMethod("creation");

        SentimentResponse analysed = new SentimentResponse(message.getPostId(), "positive");
        RestResponse<SentimentResponse> restResponse = RestResponse.ok(analysed);

        when(sentimentRestClient.analyse(new AnalyseRequest(message.getContent()))).thenReturn(restResponse);

        sentimentService.handlePost(message);

        verify(sentimentRepository, times(1)).createSentiment(any());
    }

    @Test
    public void testHandlePost_deletion_shouldCallDeleteSentiment() {
        AnalysePost message = new AnalysePost();
        message.setPostId(new ObjectId().toHexString());
        message.setContent("");
        message.setMethod("deletion");

        sentimentService.handlePost(message);

        verify(sentimentRepository, times(1)).deleteSentiment(message.getPostId());
    }

    @Test
    public void testHandlePost_unknownMethod_shouldNotCallRepository() {
        AnalysePost message = new AnalysePost();
        message.setPostId(new ObjectId().toHexString());
        message.setContent("This shouldn't trigger anything");
        message.setMethod("update");

        sentimentService.handlePost(message);

        verify(sentimentRepository, never()).createSentiment(any());
        verify(sentimentRepository, never()).deleteSentiment(any());
    }

    @Test
    public void testAnalysePost_shouldReturnNullIfRestFails() {
        String content = "Just words.";

        when(sentimentRestClient.analyse(new AnalyseRequest(content))).thenReturn(RestResponse.noContent());

        String result = sentimentService.analysePost(content);

        assertNull(result);
    }
}
