package com.epita.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents the block of a user towards another user.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Block {
    /**
     * The ID of the user who is blocking.
     */
    public String userId;

    /**
     * The ID of the user who is being blocked.
     */
    public String userBlockedId;
}
