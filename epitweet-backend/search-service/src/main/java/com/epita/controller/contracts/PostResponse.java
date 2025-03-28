package com.epita.controller.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    public String id;
    public String postType;
    public String content;
    public String mediaPath;
    public String parentId;
    public List<String> tokenizedText;

    public PostResponse(PostDocument post) {
        this.id = post.getId();
        this.postType = post.getPostType();
        this.content = post.getContent();
        this.mediaPath = post.getMediaPath();
        this.parentId = post.getParentId();
    }

    public static List<PostResponse> getPostResponses(List<PostDocument> postDocuments) {
        List<PostResponse> postResponses = new ArrayList<>();
        for (PostDocument postDocument : postDocuments) {
            postResponses.add( new PostResponse(postDocument));
        }
        return postResponses;
    }
}
