package com.epita.payloads.homeTimeline;

/**
 * - ObjectId : userId
 * - String : postType
 * - Date : postLikeDate
 * - String : method ('like', 'unlike')
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import java.util.Date;

/**
 * Represents a payload for user timeline srvc from social srvc to indicate when a like request is made.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LikeTimeLine {
    /**
     *  The id of the user
     */
    private ObjectId userId;

    /**
     *  The id of the post
     */
    private String postId;

    /**
     *  The date when the post was liked or unliked
     */
    private Date postLikeDate;

    /**
     *  The method (like, unlike)
     */
    private String method;
}