package com.epita.payloads.sentiment;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AnalyseRequest {
    private String content;
}
