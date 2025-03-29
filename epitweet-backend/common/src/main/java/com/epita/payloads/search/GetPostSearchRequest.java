package com.epita.payloads.search;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class getPostSearchRequest {
    private List<String> postIds;
}
