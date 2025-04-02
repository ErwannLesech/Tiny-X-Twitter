package com.epita.payloads.homeTimeline;


import com.epita.contracts.post.PostResponse;
import lombok.*;

/**
 * Represents a payload for Home Timeline from repo-post to add created or deleted post to timeline
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostHomeTimeline {
    /**
     * The post created or deleted with all its information
     */
    private PostResponse post;

    /**
     * The method ("creation" or "deletion")
     */
    private String method;
}
