package com.epita.controller.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a request to create or delete a like.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class AppreciationRequest {
    public boolean likeUnlike;
    public String postId;
    public String userId;
}
