package com.epita.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents the follow of a user towards another user.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Follow {
    /**
     * The ID of the user who is following.
     */
    public String userId;

    /**
     * The ID of the user who is being followed.
     */
    public String userFollowedId;
}
