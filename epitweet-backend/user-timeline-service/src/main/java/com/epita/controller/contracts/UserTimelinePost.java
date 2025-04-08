package com.epita.controller.contracts;

import lombok.*;

import java.time.Instant;

/**
 * A data transfer object representing a user's interaction with a post.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserTimelinePost {
    /**
     * ID of the related post.
     */
    private String postId;

    /**
     * Action performed by the user ("liked", "created").
     */
    private String action;

    /**
     * Timestamp of when the action occurred.
     */
    private Instant at;

}