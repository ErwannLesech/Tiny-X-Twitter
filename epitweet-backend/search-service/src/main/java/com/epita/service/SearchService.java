package com.epita.service;

import com.epita.controller.contracts.PostDocument;
import com.epita.controller.contracts.PostRequest;
import com.epita.repository.SearchRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SearchService {

    @Inject
    SearchRepository searchRepository;

    /**
     * Search posts based on the given request.
     *
     * @param request the search request.
     * @return list of matching posts.
     */
    public List<PostRequest> searchPosts(String request) {
        // FIXME: Implémentation de la recherche dans MongoDB
        List<PostDocument> documents =  searchRepository.search(request);
        List<PostRequest> results = new ArrayList<>();
        return results;
    }

    /**
     * Index a new post in MongoDB.
     *
     * @param post the post to index.
     */
    public void indexPost(PostRequest post) {
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

        // Normalize the text to remove accents and non-ASCII characters
        text = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        // Regex to keep hashtags and remove other punctuation
        List<String> tokens = Arrays.stream(text.split("\\s+"))
                .map(token -> token.startsWith("#") && token.length() > 1
                        ? token.replaceAll("[^#\\w]", "") // Remove non-word characters except #
                        : token.replaceAll("[\\p{Punct}&&[^#]]", "")) // Remove punctuation except #
                .filter(token -> !token.isBlank()) // Remove empty tokens
                .collect(Collectors.toList());

        return tokens;
    }

}
