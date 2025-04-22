package com.epita.contracts.sentiment;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SentimentResponse {
    private String postId;
    private String sentiment;
}
