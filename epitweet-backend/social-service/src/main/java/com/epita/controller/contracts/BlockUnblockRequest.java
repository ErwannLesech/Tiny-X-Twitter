package com.epita.controller.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a request to block or unblock a user.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class BlockUnblockRequest {
    public boolean blockUnblock;
    public String userBlockedId;
    public String userBlockId;
}
