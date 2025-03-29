package com.epita.payloads.search;

import com.epita.contracts.post.PostResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class getPostSearchResponse {
    private List<PostResponse> posts;
}
