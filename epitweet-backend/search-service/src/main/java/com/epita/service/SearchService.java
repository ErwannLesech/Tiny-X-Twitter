package com.epita.service;

import com.epita.contracts.post.PostResponse;
import com.epita.controller.contracts.PostDocument;
import com.epita.payloads.search.IndexPost;
import com.epita.repository.SearchRepository;
import com.epita.repository.restClient.PostRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class SearchService {

    @Inject
    SearchRepository searchRepository;

    @Inject
    @RestClient
    PostRestClient postRestClient;

    private static final Logger LOGGER = Logger.getLogger(SearchService.class.getName());

    /**
     * Search posts based on the given request.
     *
     * @param request the search request.
     * @return list of matching posts.
     */
    public List<PostResponse> searchPosts(String request) {
        List<String> tokens = tokenizeText(request);
        if (tokens.isEmpty()) {
            return new ArrayList<>();
        }
        List<PostDocument> documents =  searchRepository.search(tokens);
        LOGGER.info("Documents find: " + documents);
        List<PostResponse> posts = new ArrayList<>();
        for (PostDocument postDocument : documents) {
            RestResponse<PostResponse> restResponse = postRestClient.getPost(new ObjectId(postDocument.getPostId()));
            if (restResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                posts.add(restResponse.getEntity());
            }
        }
        return posts;
    }

    /**
     * Index a new post in MongoDB.
     *
     * @param post the post to index.
     */
    public void indexPost(IndexPost post) {
        PostDocument document = new PostDocument(post, tokenizeText(post.getContent()));
        searchRepository.indexPost(document);
    }

    /**
     * Delete a post from MongoDB by ID.
     *
     * @param postId the ID of the post to delete.
     */
    public void deletePost(String postId) {
        searchRepository.deletePost(postId);
    }

    /**
     * Tokenize the given text by lowercasing, removing punctuation, and trimming whitespace.
     *
     * @param text the text to tokenize.
     * @return list of the tokenized version of the text.
     */
    public List<String> tokenizeText(String text) {
        if (text == null) return null;

        return Arrays.stream(text.split("\\s+"))
                .flatMap(token -> {
                    // If not hashtag and contains apostrophe, split on it
                    if (!token.startsWith("#") && token.contains("'")) {
                        return Arrays.stream(token.split("'"));
                    }
                    return Stream.of(token);
                })
                .flatMap(token -> {
                    if (token.contains("#") && token.length() > 1) {
                        // Handle cases like "#test#test2" → split into ["#test", "#test2"]
                        return Arrays.stream(token.split("(?=#)"));
                    }
                    return Stream.of(token);
                })
                .map(token -> {
                    if (token.startsWith("#") && token.length() > 1) {
                        // Hashtag: remove punctuation but keep accents
                        return token.replaceAll("[^#\\p{L}\\p{N}_]", "");
                    } else {
                        // Normalize accents and lowercase for regular words
                        // remove accents
                        // remove punctuation
                        return Normalizer.normalize(token, Normalizer.Form.NFD)
                                .replaceAll("[^\\p{ASCII}]", "") // remove accents
                                .replaceAll("[\\p{Punct}&&[^#]]", "") // remove punctuation
                                .toLowerCase();
                    }
                })
                .filter(token -> !token.isBlank())
                .collect(Collectors.toList());
    }
}
