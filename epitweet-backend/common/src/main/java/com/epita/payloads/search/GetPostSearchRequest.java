package com.epita.payloads.search;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetPostSearchRequest {
    private List<String> postIds;
}
