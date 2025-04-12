package com.epita.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

/**
 * Represents a like action performed by a user on a post.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Like {
    /**
     * The ID of the user who performed the like action.
     */
    public String userId;

    /**
     * The ID of the post that was liked.
     */
    public String postsLikedId;

    /**
     * The date and time when the like action was performed.
     */
    public LocalDateTime dateTime;
}
