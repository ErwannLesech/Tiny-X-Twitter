package com.epita.controller.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostDocument {

    public String id;
    public String postType;
    public String content;
    public String mediaPath;
    public String parentId;
    public List<String> tokenizedText;

    public PostDocument() {}

    public PostDocument(PostRequest request, List<String> tokenizedText) {
        this.id = request.getId();
        this.postType = request.getPostType();
        this.content = request.getContent();
        this.mediaPath = request.getMediaPath();
        this.parentId = request.getParentId();
        this.tokenizedText = tokenizedText;
    }
}

