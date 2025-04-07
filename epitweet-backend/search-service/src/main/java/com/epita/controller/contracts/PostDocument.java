package com.epita.controller.contracts;

import com.epita.payloads.search.IndexPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostDocument {

    public String postId;
    public List<String> tokenizedText;

    public PostDocument() {}

    public PostDocument(IndexPost request, List<String> tokenizedText) {
        this.postId = request.getPostId();
        this.tokenizedText = tokenizedText;
    }
}

