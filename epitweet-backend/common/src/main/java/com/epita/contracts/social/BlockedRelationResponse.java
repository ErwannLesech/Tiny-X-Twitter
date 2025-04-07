package com.epita.contracts.social;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class BlockedRelationResponse {
    /**
     * A flag indicating whether the parent user blocked the current user.
     */
    private Boolean parentUserBlockedUser;

    /**
     * A flag indicating whether the current user blocked the parent user.
     */
    private Boolean userBlockedParentUser;
}
