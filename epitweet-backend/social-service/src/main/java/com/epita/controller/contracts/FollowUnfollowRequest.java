package com.epita.controller.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a request to follow or unfollow a user.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class FollowUnfollowRequest {
    public boolean followUnfollow;
    public String userFollowedId;
    public String userFollowId;
}
