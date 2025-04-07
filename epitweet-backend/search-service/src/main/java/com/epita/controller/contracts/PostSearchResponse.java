package com.epita.controller.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchResponse {
    public String postId;

    public PostSearchResponse(PostDocument post) {
        this.postId = post.getPostId();
    }

    public static List<PostSearchResponse> getPostResponses(List<PostDocument> postDocuments) {
        List<PostSearchResponse> postResponses = new ArrayList<>();
        for (PostDocument postDocument : postDocuments) {
            postResponses.add( new PostSearchResponse(postDocument));
        }
        return postResponses;
    }
}
