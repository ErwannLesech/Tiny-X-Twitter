package com.epita.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.epita.controller.contracts.PostDocument;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class SearchRepository {

    private static final String INDEX = "posts";

    private static final Logger LOGGER = Logger.getLogger(SearchRepository.class.getName());

    @Inject
    ElasticsearchClient client;

    /**
     * Search for posts based on a text query.
     *
     * @param tokenizedRequest the search criteria.
     * @return list of matching posts.
     */
    public List<PostDocument> search(List<String> tokenizedRequest) {
        if (tokenizedRequest.isEmpty()) {
            return new ArrayList<>();
        }
        LOGGER.info("Searching for: " + tokenizedRequest);
        try {
            // Separate words and hashtags
            List<String> hashtags = tokenizedRequest.stream()
                    .filter(word -> word.startsWith("#"))
                    .collect(Collectors.toList());

            List<String> words = tokenizedRequest.stream()
                    .filter(word -> !word.startsWith("#"))
                    .collect(Collectors.toList());

            // Build Elasticsearch query
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(INDEX)
                    .query(q -> q
                            .bool(b -> {
                                // Ensure ALL hashtags are present (strict match)
                                for (String hashtag : hashtags) {
                                    b.must(mq -> mq
                                            .term(t -> t
                                                    .field("tokenizedText.keyword") // Exact match
                                                    .value(FieldValue.of(hashtag))
                                            )
                                    );
                                }

                                // Ensure AT LEAST ONE word is present (vague match)
                                if (!words.isEmpty()) {
                                    b.should(mq -> mq
                                            .terms(t -> t
                                                    .field("tokenizedText.keyword") // Exact match
                                                    .terms(ts -> ts.value(words.stream()
                                                            .map(FieldValue::of)
                                                            .collect(Collectors.toList())))
                                            )
                                    ).minimumShouldMatch("1"); // At least one word must match
                                }

                                return b;
                            })
                    )
            );

            LOGGER.info("Executing search with tokenized request: " + tokenizedRequest);
            //LOGGER.info("Executing search with request: " + searchRequest);

            SearchResponse<PostDocument> response = client.search(searchRequest, PostDocument.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            LOGGER.severe("Failed to search posts: " + e.getMessage());
            throw new RuntimeException("Failed to search posts", e);
        }
    }

    /**
     * Index a new post in Elasticsearch.
     *
     * @param postDocument the post to index.
     */
    public void indexPost(PostDocument postDocument) {
        try {
            // Construction de la requête d'indexation dans Elasticsearch
            IndexRequest<PostDocument> request = IndexRequest.of(i -> i
                    .index(INDEX)
                    .id(postDocument.getPostId()) // Utilisation de l'ID du document comme clé
                    .document(postDocument)
            );

            IndexResponse response = client.index(request);
            LOGGER.info("Indexed post with ID: " + response.id());

        } catch (IOException e) {
            LOGGER.severe("Failed to index post: " + e.getMessage());
            throw new RuntimeException("Failed to index post", e);
        }
    }

    /**
     * Delete a post from Elasticsearch.
     *
     * @param id the ID of the post to delete.
     */
    public void deletePost(String id) {
        try {
            // Construction de la requête de suppression dans Elasticsearch
            DeleteRequest request = DeleteRequest.of(d -> d
                    .index(INDEX)
                    .id(id)
            );

            DeleteResponse response = client.delete(request);
            LOGGER.info("Deleted post with ID: " + id + " - Result: " + response.result());

        } catch (IOException e) {
            LOGGER.severe("Failed to delete post with ID: " + id + " - Error: " + e.getMessage());
            throw new RuntimeException("Failed to delete post", e);
        }
    }
}
