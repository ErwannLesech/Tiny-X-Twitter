package com.epita.controller.contracts;

import com.epita.payloads.post.CreatePostRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@ToString(callSuper = true)
public class PostRequest extends CreatePostRequest {
}
