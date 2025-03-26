package com.epita.controller.contracts;

import lombok.Getter;

import java.util.List;

@Getter
public class PostDocument {

    public String id;
    public String postType;
    public String content;
    public String mediaUrl;
    public String parentId;
    public List<String> tokenizedText;

    public PostDocument() {}

    public PostDocument(PostRequest request, List<String> tokenizedText) {
        this.postType = request.getPostType();
        this.content = request.getContent();
        this.mediaUrl = request.getMediaUrl();
        this.parentId = request.getParentId();
        this.tokenizedText = tokenizedText;
    }
}

