package com.epita.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
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
     * @param request the search criteria.
     * @return list of matching posts.
     */
    public List<PostDocument> search(String request) {
        try {
            // Construction de la requête de recherche dans Elasticsearch
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(INDEX)
                    .query(q -> q
                            .match(m -> m
                                    .field("tokenized_text")
                                    .query(request)
                            )
                    )
            );

            LOGGER.info("Executing search with query: " + request);

            // Exécuter la requête dans Elasticsearch
            SearchResponse<PostDocument> response = client.search(searchRequest, PostDocument.class);

            // Mapper la réponse Elasticsearch vers une liste de PostDocument
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
                    .id(postDocument.getId()) // Utilisation de l'ID du document comme clé
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
