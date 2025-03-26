package com.epita.controller.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PostRequest {
    public String postType;
    public String content;
    public String mediaUrl;
    public String parentId;

    public ObjectId getParentObjectId() {
        return (parentId != null && ObjectId.isValid(parentId)) ? new ObjectId(parentId) : null;
    }
}
